package com.example.miapp_grupox.api

import com.example.miapp_grupox.model.FlusingEntity

// Del servicio (DTO) a la app (Entity)
fun FlushingDto.toEntity(): FlusingEntity = FlusingEntity(
    id = (id ?: 0L).toInt(),
    granja = granja,
    galpon = galpon,
    linea = linea,
    responsable = responsable,
    motivo = motivo,
    observaciones = observaciones ?: "",
    fecha = fecha
)

// De la app (Entity) al servicio (DTO)
fun FlusingEntity.toDto(): FlushingDto = FlushingDto(
    id = if (id == 0) null else id.toLong(),
    granja = granja,
    galpon = galpon,
    linea = linea,
    responsable = responsable,
    motivo = motivo,
    observaciones = observaciones,
    fecha = fecha
)

