## Запуск приложения

1. В директории `src/main/resources` скопируйте `application-example.properties` и переименуйте в `application.properties`:
    ```bash
    cp src/main/resources/application-example.properties src/main/resources/application.properties
2. Заполните переменные в application.properties своими данными (PostgreSQL, Redis и т.д.).
   
   `APP_ENV` — определяет активный профиль Spring и уровень логирования:
   - `local` — локальная среда, логи DEBUG
   - все остальные, prod, dev - продакшн, логи INFO
3. Чувствительные доступы не класть в Git!