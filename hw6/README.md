## hw6 - RESTful.
Сервис заказа. Сервис биллинга. Сервис нотификаций

0. Установка и настройка
```
Приложения:
helm install hw6-user hw6-user-helm
helm install hw6-billing hw6-billing-helm
helm install hw6-order hw6-order-helm
helm install hw6-notifications hw6-notifications-helm
Кафка:
helm install kafka single-node-kafka
Ингресс:
kubectl apply -f api-gateway/nginx-ingress/ingress.yaml
```

1. Архитектура и схема взаимодействия
```
![скриншот](pic/schema.jpg)
```

2. Тест постмана (newman)
```
![скриншот](pic/newman_hw6.jpg)
```