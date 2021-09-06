Objectif : 

    [X] Step 1 : Mettre en place une application qui répond à : http://localhost:9000/hello -> Hi!
    [X] Step 2 : Mettre cette application dans un container docker
    [X] Step 3 : Ajouter une base de données ou la valeur sera lue 
        - [X] 3.1 : Ajouter un docker compose pour lancer une DB
        - [X] 3.2 : Mettre une lib type doobie 
        - [X] 3.3 : Construire la requête pour la servir derrière le end-point
    [] Step 4 : Mise en place l'authentification sur l'API


docker DB

docker run  -p 5432:5432 -e POSTGRES_PASSWORD=qcmiopwd -e POSTGRES_USER=qcmio postgres:10-alpine postgres -c 'max_connections=200' -c 'log_statement=all' -c 'log_line_prefix=%t[%a][%p]'