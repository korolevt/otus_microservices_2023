## hw3 - создать RESTful CRUD.
Создать приложение RESTful CRUD по созданию, удалению, просмотру и обновлению пользователей

### Приложение на Node.js  
База данных PostgeSQL, таблица пользователей:
```
CREATE TABLE IF NOT EXISTS users (
  ID SERIAL PRIMARY KEY,
  name VARCHAR(30),
  email VARCHAR(30)
);     	
```
Интерфейс приложения:
```
GET: /users | getUsers()
GET: /users/:id | getUserById()
POST: /users | createUser()
PUT: /users/:id | updateUser()
DELETE: /users/:id | deleteUser()
```
### Первый вариант установка манифестами кубернетеса (манифесты лежат в папке yaml)
Для сохранения пароля и юзера в secret
```
echo -n 'usr' | base64 (для windows: echo | set /p="usr" | openssl base64)
dXNy
echo -n 'pwd' | base64 (для windows: echo | set /p="pwd" | openssl base64)  
cHdk
```
Порядок вызова манифестов
```
kubectl apply -f postgres-configmap.yaml
kubectl apply -f postgres-secret.yaml
kubectl apply -f postgres-deployment.yaml
kubectl apply -f postgres-job.yaml
kubectl apply -f hw3.yaml
```
### Основной вариант (Helm-чарты) с помощью шаблонизации приложения в helm чартах
Создаем чарты
```
helm create hm3-chart
```
Добавляем postgresql в Chart.yaml
```
dependencies:
  - name: postgresql
    version: 12.x.x
    repository: https://charts.bitnami.com/bitnami
    condition: postgresql.enabled
```
Выполняем обновление (скачивание зависимостей)
```
helm dependency update
```
Добавлем настройки postgresql в Values.yaml
```
postgresql:
  enabled: true
  auth:
    username: usr
    password: pwd
    database: user_db
  service:
    port: "5432"
```
Добавляем Job postgres-job.yaml для создание таблицы users и заполнению таблицы несколькими записями

Проверяем схему
```
helm template hw3-chart
helm lint hw3-chart
helm install --dry-run --debug hw3 hw3-chart
```
Разворачиваем приложение
```
helm install hw3 hw3-chart	
```
Проверяем запуск всех подов (STATUS = Running и Ready = 1/1)
```
kubectl get all
```
### Итоговые запросы  
Запускаем туннель в отдельном окне (для Windows)
```
minikube tunnel 
```
Создаем запросы в Postman, экспортируем коллекцию (папка newman)

Проверяем запросы с помощью утилиты newman
```
newman run HW3.postman_collection.json
```
![](https://github.com/korolevt/otus_microservices_2023/blob/main/hw3/newman/newman_results.jpg)
## Полезные ссылки 
How to add dependency to Helm Chart
https://devpress.csdn.net/k8s/62f4e9d9c6770329307fa956.html

kubernetes. helmfile postgres
https://sidmid.ru/kubernetes-helmfile-postgres/

