/**
 * void onRemoveEntity() - удаление связей fk в сущности для последующего orphanRemoval
 * ---------------------------------В Entity--------------------------
 * add***() - добавление связи fk в сущности для OneToMany и ManyToMany
 * remove***() - удаление связи fk в сущности для OneToMany и ManyToMany
 * set***() - добавление связи fk в сущности для ManyToOne
 * Для комментариев достаточно удаление remove*** - удаляет связь между event и comment или comment и comment to comment
 * т.е. связи дальше в глубину не удаляются, но они и не ссылаются вовне
 * Можно удалять и с помощью onRemoveEntity - тогда находятся и удаляются все связи внутри, но для комментариев здесь это излишне,
 * только в целях тренировки
 */
package ru.practicum.explorewithme;

public interface EntityInterfaces {
    //Функция для удаления связей между сущностями перед удалением
    void onRemoveEntity();
}
