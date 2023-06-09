[pull request for rewiever](https://github.com/AndrSlav13/java-explore-with-me/pull/9)
# Дипломная работа по курсу "Java-разработчик"
### Яндекс Практикум: проект "Explore With Me"
### Технологии: Java + Spring Boot + Maven + Lombok + JUnit + RESTful API + PostgreSQL + Docker +    ORM (HIBERNATE)
### Целью проекта является разработка части API для обмена информацией о событиях[^1], разработка функционала CRUD для 
- добавления информации о событиях, 
- получения информации о событиях из базы данных,
- поиска событий по различным параметрам,
- организации посещения мероприятий.
 [^1]: Здесь событие - источник информации, который может заинтересовать людей, и который не может быть представлен во всем объёме для использования в формате носителя информации (выставка, лекция, концерт и т.д.), и/или материалы по которому могут быть полезны для использования в любом формате (запись концерта/лекции, отчет по походу и т.д.).

### Схема ORM модели данных
![Схема ORM модели данных](/orm_schema.png)

### Здесь:
- events - событие,
- categories - категория события,
- users - пользователи,
- compilations - события, объединенные в группы по параметрам,
- requests - заявки пользователей для участия в событии,
- comments - комментарии пользователей к событиям,
- comment_details - информация, используемая при модерации комментариев,
- event_compilation - таблица связности для таблиц "events" и "compilations"
- statistics - таблица для сохранения служебной информации об активности пользователей при просмотре информации о событиях.
