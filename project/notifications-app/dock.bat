docker build -t pr-notifications-app .
docker tag pr-notifications-app tvv766/pr-notifications-app
docker push tvv766/pr-notifications-app