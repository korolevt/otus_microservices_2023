## hw1 - докер
Для Веб-сервера выбрана версия node:slim

Создание докера
```bash
docker build -t hw1 .
```
Создание докера с указанием платформы linux/amd64
```bash
docker build --platform linux/amd64 -t hw1 .
```

Запуск докера
```bash
docker run -d -p 8000:8000 --name hw1 hw1
```
Проверка работы
```bash
curl http://localhost:8000/health
```
Push в DockerHub
```bash
docker tag hw1 tvv766/hw1
docker push tvv766/hw1
```
Скачать докер-образ из реестра:
```bash
docker pull tvv766/hw1
```
