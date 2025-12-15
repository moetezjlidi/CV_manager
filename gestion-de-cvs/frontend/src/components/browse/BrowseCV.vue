<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2><i class="bi bi-list"></i> Parcourir les CVs</h2>
      <div>
        <a href="#/search" class="btn btn-outline-primary me-2">
          <i class="bi bi-search"></i> Recherche avancée
        </a>
        <a v-if="user && user.authenticated" href="#/person/new" class="btn btn-success">
          <i class="bi bi-person-plus"></i> Coopter un CV
        </a>
      </div>
    </div>

    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-6">
            <input 
              type="text" 
              class="form-control" 
              placeholder="🔍 Filtrer la page affichée par nom" 
              v-model="quickFilter"
            />
          </div>
          <div class="col-md-6 text-end">
            <span class="text-muted">
              {{ totalElements }} CV(s) au total • Page {{ currentPage + 1 }}/{{ totalPages }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
    </div>
    
    <div v-else-if="persons.length === 0" class="alert alert-info">
      <i class="bi bi-info-circle"></i> Aucun CV trouvé.
    </div>
    

    <div v-else class="row">
      <div v-for="person in paginatedPersons" :key="person.id" class="col-md-6 col-lg-4 mb-4">
        <div class="card h-100 shadow-sm person-card" @click="viewPerson(person.id)">
          <div class="card-body">
            <div class="d-flex align-items-center mb-3">
              <div class="avatar-circle me-3">
                {{ getInitials(person.firstName, person.lastName) }}
              </div>
              <div>
                <h5 class="card-title mb-0">{{ person.firstName }} {{ person.lastName }}</h5>
                <p class="text-muted small mb-0">
                  <i class="bi bi-envelope"></i> {{ person.email }}
                </p>
              </div>
            </div>
            
            <div class="mb-3">
              <span class="badge bg-info">
                <i class="bi bi-briefcase"></i> 
                {{ person.activities ? person.activities.length : 0 }} activité(s)
              </span>
            </div>
            
            <button class="btn btn-sm btn-primary w-100" @click.stop="viewPerson(person.id)">
              <i class="bi bi-eye"></i> Voir le CV
            </button>
          </div>
        </div>
      </div>
    </div>

    <nav v-if="totalPages > 1" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <button class="page-link" @click="currentPage--" :disabled="currentPage === 0">
            <i class="bi bi-chevron-left"></i> Précédent
          </button>
        </li>
        <li 
          v-for="page in visiblePages" 
          :key="page" 
          class="page-item" 
          :class="{ active: currentPage === page }"
        >
          <button class="page-link" @click="currentPage = page">{{ page + 1 }}</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
          <button class="page-link" @click="currentPage++" :disabled="currentPage === totalPages - 1">
            Suivant <i class="bi bi-chevron-right"></i>
          </button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  inject: ['user'],
  data() {
    return {
      persons: [],
      loading: true,
      quickFilter: '',
      currentPage: 0,
      pageSize: 20,
      totalElements: 0,
      totalPages: 0,
      axiosInstance: null
    };
  },
  computed: {
    paginatedPersons() {
      if (!this.quickFilter) {
        return this.persons;
      }
      
      const query = this.quickFilter.toLowerCase();
      return this.persons.filter(p => 
        p.firstName.toLowerCase().includes(query) ||
        p.lastName.toLowerCase().includes(query) ||
        p.email.toLowerCase().includes(query)
      );
    },
    
    visiblePages() {
      const pages = [];
      const maxVisible = 5;
      let start = Math.max(0, this.currentPage - Math.floor(maxVisible / 2));
      let end = Math.min(this.totalPages - 1, start + maxVisible - 1);
      
      if (end - start < maxVisible - 1) {
        start = Math.max(0, end - maxVisible + 1);
      }
      
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      return pages;
    }
  },
  watch: {
    currentPage() {
      this.loadPersons();
    }
  },
  mounted() {
    this.axiosInstance = axios.create({
      baseURL: '/api/',
      headers: { 'Content-Type': 'application/json' },
      timeout: 5000,
    });
    this.loadPersons();
  },
  methods: {
    loadPersons() {
      this.loading = true;
      
      this.axiosInstance.get('persons', {
        params: {
          page: this.currentPage,
          size: this.pageSize
        }
      })
        .then(response => {
          this.persons = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
          this.loading = false;
        })
        .catch(err => {
          console.error('Erreur:', err);
          alert('Impossible de charger les CVs');
          this.loading = false;
        });
    },
    
    viewPerson(id) {
      window.location.hash = `#/person/${id}`;
    },
    
    getInitials(firstName, lastName) {
      return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('fr-FR', { year: 'numeric', month: 'long', day: 'numeric' });
    }
  }
}
</script>

<style scoped>
.person-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.person-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2) !important;
}

.avatar-circle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2rem;
}
</style>
