# Utilisation d'une image Nginx légère basée sur Alpine Linux
FROM nginx:alpine

# Supprimer la configuration et les fichiers par défaut de Nginx
RUN rm -rf /usr/share/nginx/html/*

# Copier les fichiers HTML vers le répertoire Nginx
COPY test.html /usr/share/nginx/html/index.html
COPY test.html /usr/share/nginx/html/test.html

# 👉 NOUVELLE LIGNE : On ajoute le dashboard !
COPY dashboard.html /usr/share/nginx/html/dashboard.html

# Exposer le port 80
EXPOSE 80

# Lancer Nginx
CMD ["nginx", "-g", "daemon off;"]