# Organisation des Composants

## Structure des Dossiers

```
components/
├── browse/          # PARTIE 1: Parcours des CVs (Public)
│   ├── BrowseCV.vue       # Liste paginée des CVs
│   ├── ViewCV.vue         # Détail d'un CV
│   └── SearchCV.vue       # Recherche avancée
│
├── auth/            # PARTIE 2: Authentification
│   ├── Login.vue          # Connexion JWT
│   └── Logout.vue         # Déconnexion
│
├── my-cv/           # PARTIE 3: Modification de CV (Authentification requise)
│   ├── MyCV.vue           # Consultation de son CV
│   ├── EditMyCV.vue       # Édition de son CV
│   └── MyActivities.vue   # Gestion CRUD des activités
│
├── cooptation/      # PARTIE 4: Création de Personne (Authentification requise)
│   └── CooptPerson.vue    # Formulaire de cooptation
│
└── common/          # Composants communs
    └── NotFound.vue       # Page 404
```

## Composants par Partie

### 🔍 browse/ - Parcours des CVs (3 composants)
- **BrowseCV.vue**: Liste principale avec pagination, filtres et statistiques
- **ViewCV.vue**: Affichage détaillé d'un CV avec timeline des activités
- **SearchCV.vue**: Recherche duale (par nom ou par activité)

### 🔐 auth/ - Authentification (2 composants)
- **Login.vue**: Formulaire de connexion avec JWT
- **Logout.vue**: Déconnexion et nettoyage de session

### ✏️ my-cv/ - Mon CV (3 composants)
- **MyCV.vue**: Vue de consultation de son propre CV
- **EditMyCV.vue**: Formulaire d'édition (email non modifiable)
- **MyActivities.vue**: CRUD complet pour gérer ses activités

### 👤 cooptation/ - Cooptation (1 composant)
- **CooptPerson.vue**: Création de CV pour une personne sans compte

### 🔧 common/ - Communs (1 composant)
- **NotFound.vue**: Page d'erreur 404

## Composants Supprimés (Obsolètes)

Les composants suivants ont été supprimés car remplacés par la nouvelle structure :

- ❌ `ActivitiesList.vue` → Remplacé par `MyActivities.vue`
- ❌ `ActivityDetails.vue` → Intégré dans `ViewCV.vue`
- ❌ `ActivityForm.vue` → Intégré dans `MyActivities.vue`
- ❌ `CVApp.vue` → Remplacé par le routing dans `App.vue`
- ❌ `Limited.vue` → Non utilisé
- ❌ `Message.vue` → Non utilisé
- ❌ `PersonDetails.vue` → Remplacé par `ViewCV.vue`
- ❌ `PersonForm.vue` → Remplacé par `CooptPerson.vue`
- ❌ `PersonsList.vue` → Remplacé par `BrowseCV.vue`
- ❌ `SavedActivityInfo.vue` → Non utilisé
- ❌ `Upper.vue` → Non utilisé
- ❌ `Whoami.vue` → Non utilisé

## Imports dans App.vue

```javascript
// PARTIE 1: PARCOURS
import BrowseCV from "@/components/browse/BrowseCV.vue";
import ViewCV from "@/components/browse/ViewCV.vue";
import SearchCV from "@/components/browse/SearchCV.vue";

// PARTIE 2: AUTHENTIFICATION
import Login from "@/components/auth/Login.vue";
import Logout from "@/components/auth/Logout.vue";

// PARTIE 3: MODIFICATION
import MyCV from "@/components/my-cv/MyCV.vue";
import EditMyCV from "@/components/my-cv/EditMyCV.vue";
import MyActivities from "@/components/my-cv/MyActivities.vue";

// PARTIE 4: COOPTATION
import CooptPerson from "@/components/cooptation/CooptPerson.vue";

// COMMUNS
import NotFound from "@/components/common/NotFound.vue";
```

## Principes d'Organisation

### 1. Séparation par Fonctionnalité
Chaque dossier correspond à une partie fonctionnelle de l'application selon les contraintes académiques.

### 2. Composants Standalone
Tous les composants de route sont autonomes :
- Chargent leurs propres données via axios
- Injectent le contexte `user` via `inject: ['user']`
- Gèrent leur propre navigation

### 3. Nomenclature Claire
- Noms descriptifs et explicites
- Préfixes significatifs (Browse, View, Edit, My...)
- Cohérence dans le nommage

### 4. Pas de Duplication
- Un seul composant par fonctionnalité
- Suppression des anciens composants non utilisés
- Code DRY (Don't Repeat Yourself)

## Statistiques

- **Total composants actifs**: 10
- **Composants supprimés**: 12
- **Réduction**: -54% de composants
- **Structure**: 5 dossiers organisés

## Maintenance

### Ajouter un Nouveau Composant

1. Identifier la partie concernée (browse, auth, my-cv, cooptation, common)
2. Créer le fichier dans le bon dossier
3. Importer dans `App.vue` si c'est une route
4. Mettre à jour cette documentation

### Supprimer un Composant

1. Retirer l'import dans `App.vue`
2. Retirer de la configuration des routes
3. Supprimer le fichier
4. Mettre à jour cette documentation
