package com.cloud.voiture.crud.controller;

import com.cloud.voiture.crud.model.GenericModel;
import com.cloud.voiture.crud.pagination.Paginated;
import com.cloud.voiture.crud.service.GenericService;
import com.cloud.voiture.models.voiture.Etat;
import com.cloud.voiture.services.utilities.Utilities;
import com.cloud.voiture.types.response.Response;

import jakarta.validation.Valid;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public class GenericController<T extends GenericModel> {
    private final Class<T> type;
    @Autowired
    GenericService<T> service;

    HashMap<String, Object> res;
    HashMap<String, Object> err;

    public GenericController() {
        res = new HashMap<>();
        type = (Class<T>) ResolvableType.forClass(getClass()).getSuperType().getGeneric(0).resolve();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> find(@PathVariable(name = "id") int id) {
        try {
            T results = service.find(id);
            return ResponseEntity.ok(new Response(results, ""));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new Response("Cette identifiant n'existe pas."));
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody T model) {
        try {
            T results = service.save(model);
            return ResponseEntity.ok(new Response(results, "Inséré avec succes"));
        } catch (DataIntegrityViolationException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException sqlException = (ConstraintViolationException) e.getCause();
                String sqlState = sqlException.getSQLState();
                String columnName = Utilities.extractColumnName(
                        type,
                        sqlException.getMessage());

                System.out.println("================== " + sqlException.getMessage());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(
                                SqlErrorMessage.getMessage(sqlState, columnName,
                                        type.getSimpleName())));

            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new Response("Contrainte de donnée violée"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new Response("Erreur interne du serveur"));
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "0") int pageSize) {
        System.out.println("Page " + page + " taille " + pageSize + "=============");
        try {
            if (page == 0 && pageSize == 0) {
                List<T> results = service.findAll();
                return ResponseEntity.ok(new Response(results, ""));
            }
            Paginated<T> result = service.findAll(page, pageSize);
            return ResponseEntity.ok(new Response(result, ""));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> save(
            @RequestBody @Valid T model,
            @PathVariable(name = "id") int id) {

        try {
            T results = service.update(model, id);
            return ResponseEntity.ok(new Response(results, "Modifié avec succes"));

        } catch (DataIntegrityViolationException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException sqlException = (ConstraintViolationException) e.getCause();
                String sqlState = sqlException.getSQLState();
                String columnName = Utilities.extractColumnName(
                        type,
                        sqlException.getMessage());
                System.out.println(sqlException.getMessage());
                System.out.println(columnName + "==================");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(
                                SqlErrorMessage.getMessage(sqlState, columnName,
                                        type.getSimpleName())));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new Response("Contrainte de donnée violée"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new Response("Erreur interne du serveur"));
        }
    }

    public GenericService<T> getService() {
        return service;
    }

    public HashMap<String, Object> getRes() {
        return res;
    }

    public HashMap<String, Object> getErr() {
        return err;
    }
}
