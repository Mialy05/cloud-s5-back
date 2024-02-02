package com.cloud.voiture.services.annonce;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.cloud.voiture.models.annonce.AnnonceEtFavori;
import com.cloud.voiture.models.annonce.favori.Favori;
import com.cloud.voiture.models.annonce.favori.FavoriAnnonceID;
import com.cloud.voiture.repositories.annonce.FavoriRepository;
import com.cloud.voiture.services.UtilisateurService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.auth.message.AuthException;

@Service
public class FavoriService {
    @Autowired
    FavoriRepository favoriRepository;

    @Autowired
    UtilisateurService utilisateurService;
    @PersistenceContext
    EntityManager entityManager;

    private static int TAILLE_PAGE = 5;

    public List<AnnonceEtFavori> findFavoriOf(int idUtilisateur, int page, int taille) throws AuthException {
        if (page == 0) {
            return entityManager.createNativeQuery(
                    """
                            select a.*, f.date_maj, f.date_ajout
                            from v_annonce_general a
                                join v_annonce_favori f
                                on a.id = f.id_annonce and a.status = f.status
                                where f.id_utilisateur = :user order by date_ajout desc
                            """,
                    AnnonceEtFavori.class).setParameter("user", idUtilisateur).getResultList();
        }
        if (taille == 0) {
            taille = TAILLE_PAGE;
        }
        return entityManager.createNativeQuery(
                """
                        select a.*, f.date_maj, f.date_ajout
                        from v_annonce_general a
                        join v_annonce_favori f
                        on a.id = f.id_annonce and a.status = f.status
                        where f.id_utilisateur = :user order by date_ajout desc limit :taille offset(:numero - 1)*:taille
                        """,
                AnnonceEtFavori.class).setParameter("user", idUtilisateur)
                .setParameter("taille", taille)
                .setParameter("numero", page)
                .getResultList();

    }

    public Favori findByAnnonceAndUtilisateur(int idAnnonce, int idUtilisateur) {
        return favoriRepository.findById(new FavoriAnnonceID(idUtilisateur, idAnnonce)).orElse(null);
    }

    public AnnonceEtFavori existOrLiked(int idUtilisateur, int idAnnonce) throws NotFoundException {
        List<AnnonceEtFavori> f = entityManager
                .createNativeQuery(
                        """
                                    select a.id , a.id_utilisateur, a.date_maj, f.date_ajout, a.status from v_annonce_gen_a_jour a left join annonce_favori f on f.id_annonce = a.id  and f.id_utilisateur = :user where a.id = :annonce
                                """,
                        AnnonceEtFavori.class)
                .setParameter("user", idUtilisateur)
                .setParameter("annonce", idAnnonce)
                .getResultList();

        if (f.size() == 0) {
            throw new NotFoundException();
        } else {
            return f.get(0);
        }
    }

    public void save(Favori f) {
        favoriRepository.insert(f.getIdUtilisateur(), f.getIdAnnonce());
    }

    public void delete(Favori f) {
        favoriRepository.delete(f);
    }
}
