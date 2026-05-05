# Gestion Suivi & Évaluation Service - UASZ

Microservice Spring Boot pour la partie :

## 4.5 Suivi et évaluation

- Indicateurs d’avancement
- Historique des actions
- Génération de rapports PDF / Excel

## Port

```txt
8085
```

## Base de données

```txt
gestion_suivi_evaluation_db
```

## Microservices utilisés

```txt
Encadrement : http://localhost:8083/api
Livrables   : http://localhost:8084/api
```

## Règles métier

- L'enseignant encadreur crée le suivi du projet.
- L'enseignant encadreur évalue le projet.
- Le système calcule automatiquement :
  - avancement actuel
  - nombre de suivis
  - nombre de livrables
  - nombre de livrables évalués
  - moyenne des livrables
  - niveau de risque
  - statut du projet
- Les actions importantes sont historisées.
- Les rapports sont générés en PDF et Excel.

## Lancement

```bash
mvn clean install
mvn spring-boot:run
```

## Créer un suivi

```http
POST http://localhost:8085/api/suivi
Authorization: Bearer TOKEN_ENSEIGNANT
```

```json
{
  "encadrementId": 1,
  "avancementPourcentage": 45,
  "qualiteTravail": 15,
  "respectDelais": 14,
  "participationEtudiant": 16,
  "observations": "Bon avancement du projet.",
  "recommandations": "Améliorer la partie conception UML."
}
```

## Évaluer un projet

```http
POST http://localhost:8085/api/evaluations
Authorization: Bearer TOKEN_ENSEIGNANT
```

```json
{
  "encadrementId": 1,
  "noteGlobale": 16,
  "appreciation": "Projet bien structuré.",
  "pointsForts": "Bonne architecture microservices.",
  "pointsAAmeliorer": "Tests unitaires à compléter."
}
```

## Voir les indicateurs

```http
GET http://localhost:8085/api/indicateurs/1
Authorization: Bearer TOKEN
```

## Historique des actions

```http
GET http://localhost:8085/api/historique/encadrement/1
Authorization: Bearer TOKEN
```

## Générer rapport PDF

```http
GET http://localhost:8085/api/rapports/1/pdf
Authorization: Bearer TOKEN_ADMIN_OU_ENSEIGNANT
```

## Générer rapport Excel

```http
GET http://localhost:8085/api/rapports/1/excel
Authorization: Bearer TOKEN_ADMIN_OU_ENSEIGNANT
```
