## hw2 - Основы работы с Kubernetes
Манифесты должны описывать сущности: Deployment, Service, Ingress.
В Deployment могут быть указаны Liveness, Readiness проб

### Установка minikube на Windows используя Docker 
Используем choco
```
choco install minikube
```
Запускаем minikube
```
minikube start --driver=docker
```
Проверяем результат установки (список запущенных в кластере подов, под всеме namespaces)
```
kubectl get pods --all-namespaces
```
Устанавливаем helm
```
choco install kubernetes-helm
```
Устанавливаем Ingress
```
kubectl create namespace m && helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx/ && helm repo update && helm install nginx ingress-nginx/ingress-nginx --namespace m -f nginx-ingress.yaml
```
Запускаем манифист 
В одном манифесте прописаны стразу три сущности Deployment, Service и Ingress.
В Deploment указаны Livenss и Readiness пробы
```
kubectl apply -f hw2.yaml
```
Ждем запуска двух подов (STATUS = Running и Ready = 1/1)
```
kubectl get pods
```
### Для Windows 
Запускаем туннель в отдельном окне 
```
minikube tunnel 
```
Проверяем запрос и прописываем в C:\Windows\System32\drivers\etc\hosts хост arch.homework с адресом 127.0.0.1
```
curl --resolve arch.homework:80:127.0.0.1 -i http://arch.homework:80/health
```
### Для Linux
Узнаем ip адрес minukube 
```
minikube ip 
```
Проверяем запрос и прописываем в /etc/hosts хост arch.homework с адресом миникуба (minikube ip)
```
curl --resolve arch.homework:80:$(minikube ip) -i http://arch.homework:80/health
```

### Дополнительно в Ingress-е добавлено правило, которое форвардит все запросы с /otusapp/{student name}/* на сервис с rewrite-ом пути. Где {student name} - это имя студента
`
nginx.ingress.kubernetes.io/configuration-snippet: |
      rewrite ^/health(/?)$ /health break;
      rewrite ^/otusapp/(.+)/(.*) /$2 break;
`

### Итоговые запросы
`
curl http://arch.homework/health
curl arch.homework/otusapp/tkorolev/health
`

## Полезные ссылки 
Настройка Liveness, Readiness и Startup проб
https://kubernetes.io/ru/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/

Ingress
https://kubernetes.io/docs/concepts/services-networking/ingress/

Шпаргалка по kubectl
https://kubernetes.io/ru/docs/reference/kubectl/cheatsheet/

Using kubectl to Create an Nginx Ingress
https://support.huaweicloud.com/intl/en-us/my-kualalumpur-1-usermanual-cce/cce_01_0364.html