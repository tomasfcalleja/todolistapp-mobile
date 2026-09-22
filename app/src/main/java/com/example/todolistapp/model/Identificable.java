package com.example.todolistapp.model;

/**
 * Todo lo que se guarda en un JsonRepository tiene que poder identificarse
 * por id. Es lo que le permite al repositorio generico buscar, actualizar
 * y borrar sin saber con que clase esta trabajando.
 */
public interface Identificable {

    int getId();

    void setId(int id);
}