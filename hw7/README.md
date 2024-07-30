## hw7 - Распределенные транзакции
Реализовать сервисы "Платеж", "Склад", "Доставка".

0. Установка и настройка
```
Приложения:
helm install hw7-order hw7-order-helm
helm install hw7-payments hw7-payments-helm
helm install hw7-inventory hw7-inventory-helm
helm install hw7-shipment hw7-shipment-helm
Jaeger:
helm repo add jaeger-all-in-one https://raw.githubusercontent.com/hansehe/jaeger-all-in-one/master/helm/charts
helm install jaeger jaeger-all-in-one/jaeger-all-in-one
Ингресс:
kubectl apply -f api-gateway/nginx-ingress/ingress.yaml
```

1. Архитектура и схема взаимодействия
Сага, основанная на оркестровке (Orchestration) 

![скриншот](pic/schema.jpg)


2. Тест постмана (newman)

![скриншот](pic/newman.jpg)


3. Пример успешного выполнение Саги

![скриншот](pic/success.jpg)


4. Пример отката бизнес-транзакции (при неуспехе выделить курьера - откат всех остальных изменений)

![скриншот](pic/error.jpg)


6. Примеры отображения трассировки транзакции (и спаны) в системе Jaeger

![скриншот](pic/jaeger1.jpg)
![скриншот](pic/jaeger2.jpg)


