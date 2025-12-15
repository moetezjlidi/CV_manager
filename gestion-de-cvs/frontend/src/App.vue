<template>
  <div class="container">

    <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
      <div class="container-fluid">
        <a class="navbar-brand" href="#/">
          <i class="bi bi-file-person"></i> Gestion de CVs
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <!-- 1. PARCOURS DES CVs -->
            <li class="nav-item">
              <a class="nav-link" :class="{ active: isActive('/') || isActive('/browse') }" href="#/">
                <i class="bi bi-list"></i> Parcourir
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" :class="{ active: isActive('/search') }" href="#/search">
                <i class="bi bi-search"></i> Rechercher
              </a>
            </li>
            
            <!-- 2. AUTHENTIFICATION -->
            <li class="nav-item" v-if="!user.authenticated">
              <a class="nav-link" :class="{ active: isActive('/login') }" href="#/login">
                <i class="bi bi-box-arrow-in-right"></i> Connexion
              </a>
            </li>
            
            <!-- 4. CRÉATION PERSONNE (si authentifié) -->
            <li class="nav-item" v-if="user.authenticated">
              <a class="nav-link" :class="{ active: isActive('/person/new') }" href="#/person/new">
                <i class="bi bi-person-plus"></i> Coopter quelqu'un
              </a>
            </li>

            <!-- 3. MODIFICATION CV (si authentifié) -->
            <li class="nav-item dropdown" v-if="user.authenticated">
              <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" 
                 data-bs-toggle="dropdown" aria-expanded="false">
                <i class="bi bi-pencil-square"></i> {{user.name}}
              </a>
              <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                <li><a class="dropdown-item" href="#/my-cv/view">
                  <i class="bi bi-eye"></i> Voir mon CV
                </a></li>
                <li><a class="dropdown-item" href="#/my-cv/edit">
                  <i class="bi bi-pencil"></i> Modifier mes informations
                </a></li>
                <li><a class="dropdown-item" href="#/my-cv/activities">
                  <i class="bi bi-briefcase"></i> Gérer mes activités
                </a></li>
              </ul>
            </li>         
          </ul>
          
          <!-- Barre de droite -->
          <ul class="navbar-nav">
            <li class="nav-item" v-if="user.authenticated">
              <a class="nav-link" href="#/logout">
                <i class="bi bi-box-arrow-right"></i> Déconnexion
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <component :is="currentView"/>
  </div>
</template>

<style scoped>
</style>

<script setup>
// ========== PARTIE 1: PARCOURS DES CVs ==========
import BrowseCV from "@/components/browse/BrowseCV.vue";
import ViewCV from "@/components/browse/ViewCV.vue";
import SearchCV from "@/components/browse/SearchCV.vue";

// ========== PARTIE 2: AUTHENTIFICATION ==========
import Login from "@/components/auth/Login.vue";
import Logout from "@/components/auth/Logout.vue";

// ========== PARTIE 3: MODIFICATION CV ==========
import MyCV from "@/components/my-cv/MyCV.vue";
import EditMyCV from "@/components/my-cv/EditMyCV.vue";
import MyActivities from "@/components/my-cv/MyActivities.vue";

// ========== PARTIE 4: CRÉATION PERSONNE (Cooptation) ==========
import CooptPerson from "@/components/cooptation/CooptPerson.vue";

// ========== PARTIE 5: INVITATIONS ==========
import AcceptInvitation from "@/components/invitation/AcceptInvitation.vue";

// ========== AUTRES ==========
import NotFound from "@/components/common/NotFound.vue";
import User from "@/user.js";
import {ref, provide, computed} from 'vue';

const currentTab = ref(window.location.hash);
const user = ref(new User());
provide('user', user);

const routes = {
  // PARTIE 1: PARCOURS DES CVs
  '/': BrowseCV,
  '/browse': BrowseCV,
  '/person/:id': ViewCV,
  '/search': SearchCV,
  
  // PARTIE 2: AUTHENTIFICATION
  '/login': Login,
  '/logout': Logout,
  
  // PARTIE 3: MODIFICATION CV (authentification requise)
  '/my-cv/view': MyCV,
  '/my-cv/edit': EditMyCV,
  '/my-cv/activities': MyActivities,
  
  // PARTIE 4: CRÉATION PERSONNE (cooptation)
  '/person/new': CooptPerson,
  
  // PARTIE 5: INVITATIONS
  '/invitation/:token': AcceptInvitation,
};

// Fonction pour vérifier si une route est active
const isActive = (route) => {
  const current = currentTab.value.slice(1) || '/';
  return current === route || current.startsWith(route + '/');
};

const currentView = computed(() => {
  const path = currentTab.value.slice(1) || '/';
  

  if (routes[path]) {
    return routes[path];
  }
  

  for (const routePath in routes) {
    if (routePath.includes(':')) {
      const regex = new RegExp('^' + routePath.replace(/:\w+/g, '[\\w-]+') + '$');
      if (regex.test(path)) {
        return routes[routePath];
      }
    }
  }
  
  return NotFound;
});

window.addEventListener('hashchange', () => {
  currentTab.value = window.location.hash;
});

defineExpose({ isActive });
</script>
