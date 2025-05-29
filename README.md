# MyTripMate

![MyTripMate Logo](/placeholder.svg?height=100&width=100)

## Votre compagnon de voyage intelligent

MyTripMate est une application Android intuitive conçue pour accompagner les voyageurs dans toutes les étapes de leur voyage : planification, rappels, météo, souvenirs et inspiration quotidienne.

## Table des matières

- [Description](#description)
- [Fonctionnalités](#fonctionnalités)
- [Technologies utilisées](#technologies-utilisées)
- [Architecture](#architecture)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [API](#api)
- [Captures d'écran](#captures-décran)
- [Améliorations futures](#améliorations-futures)
- [Contributeurs](#contributeurs)
- [Licence](#licence)

## Description

Voyager, c'est découvrir, s'évader, et vivre de nouvelles expériences. Cependant, organiser un voyage peut vite devenir stressant sans outils adaptés. MyTripMate rend chaque aventure plus simple, plus organisée et plus mémorable grâce à son interface conviviale et ses fonctionnalités pratiques.

### Objectifs du projet

- Simplifier l'organisation personnelle des voyages
- Offrir des rappels automatiques avant les départs
- Permettre la sauvegarde élégante de souvenirs
- Proposer des destinations populaires à découvrir
- Fournir une expérience fluide, rapide et agréable

## Fonctionnalités

| Fonctionnalité | Description |
|----------------|-------------|
| Gestion des utilisateurs | Connexion / Inscription sécurisées |
| Planification de voyage | Ajouter voyages avec dates, lieux et notes |
| Notifications de rappel | Alertes automatiques avant les départs |
| Consultation de la météo | Météo en temps réel des destinations |
| Citations inspirantes | Citation de motivation quotidienne |
| Souvenirs de voyage | Gestion de photos et textes souvenirs |
| Découverte de destinations | Liste de lieux populaires à visiter |

## Technologies utilisées

- **Langage**: Java
- **Base de données locale**: Room (SQLite)
- **Réseau**: Retrofit (API météo + citations)
- **UI/UX**: RecyclerView, CardView, Material Design
- **Notifications**: AlarmManager + BroadcastReceiver
- **Appareil photo**: Intégration de CameraX pour capturer des photos

## Architecture

MyTripMate utilise une architecture MVC améliorée:

```
├── Models
│   ├── User
│   ├── Trip
│   ├── Souvenir
│   └── TopPlace
├── Views
│   ├── Activities
│   │   ├── LoginActivity
│   │   ├── RegisterPage
│   │   ├── MainActivity
│   │   ├── TripPlannerActivity
│   │   ├── SouvenirsActivity
│   │   └── TopPlacesActivity
│   ├── Adapters
│   │   ├── TripAdapter
│   │   ├── SouvenirAdapter
│   │   └── TopPlacesAdapter
│   └── Fragments
├── Controllers
│   ├── Database
│   │   ├── TripDatabase
│   │   ├── UserDAO
│   │   ├── TripDAO
│   │   └── SouvenirDAO
│   ├── API
│   │   ├── WeatherApiService
│   │   └── QuoteApiService
│   └── Receivers
│       └── NotificationReceiver
```

### Flux de navigation

![Flux de navigation](/placeholder.svg?height=300&width=500)

## Installation

1. Clonez ce dépôt
```bash
git clone https://github.com/username/MyTripMate.git
