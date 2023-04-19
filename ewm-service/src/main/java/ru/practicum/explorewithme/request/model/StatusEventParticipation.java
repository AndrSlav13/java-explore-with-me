package ru.practicum.explorewithme.request.model;

public enum StatusEventParticipation {   //Заявка на участие
    PENDING/*default*/, CANCELED,    //Устанавливаются пользователем
    CONFIRMED, REJECTED;    //Устанавливаются модератором
}
