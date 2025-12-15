<template>
  <div>
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle"></i> {{ error }}
      <br/>
      <button @click="goBack" class="btn btn-sm btn-secondary mt-2">
        <i class="bi bi-arrow-left"></i> Retour
      </button>
    </div>

    <div v-else-if="person" class="person-details">
      <div class="card shadow">
        <!-- En-tête du CV -->
        <div class="card-header bg-gradient-primary text-white">
          <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center">
              <div class="avatar-large me-3">
                {{ getInitials(person.firstName, person.lastName) }}
              </div>
              <div>
                <h2 class="mb-0">{{ person.firstName }} {{ person.lastName }}</h2>
                <p class="mb-0"><i class="bi bi-envelope"></i> {{ person.email }}</p>
              </div>
            </div>
            <button @click="goBack" class="btn btn-light">
              <i class="bi bi-arrow-left"></i> Retour
            </button>
          </div>
        </div>
        
        <!-- Informations personnelles -->
        <div class="card-body">
          <div class="row mb-4">
            <div class="col-md-6">
              <h5><i class="bi bi-person-badge"></i> Informations Personnelles</h5>
              <ul class="list-unstyled">
                <li><strong>Nom :</strong> {{ person.lastName }}</li>
                <li><strong>Prénom :</strong> {{ person.firstName }}</li>
                <li><strong>Email :</strong> {{ person.email }}</li>
                <li v-if="person.birthDate">
                  <strong>Date de naissance :</strong> {{ formatDate(person.birthDate) }}
                </li>
                <li v-if="person.website">
                  <strong>Site Web :</strong> 
                  <a :href="person.website" target="_blank" rel="noopener noreferrer">
                    {{ person.website }}
                  </a>
                </li>
              </ul>
            </div>
            
            <div class="col-md-6">
              <h5><i class="bi bi-graph-up"></i> Statistiques</h5>
              <div class="stats">
                <div class="stat-item">
                  <div class="stat-number">{{ getTotalActivities() }}</div>
                  <div class="stat-label">Activités au total</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ getActivitiesByNature('EXPERIENCE_PROFESSIONAL') }}</div>
                  <div class="stat-label">Expériences Pro</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ getActivitiesByNature('TRAINING') }}</div>
                  <div class="stat-label">Formations</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ getActivitiesByNature('PROJECTS') }}</div>
                  <div class="stat-label">Projets</div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Liste des activités -->
          <div class="activities-section">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h4><i class="bi bi-briefcase-fill"></i> Activités & Parcours</h4>
            </div>
            
            <div v-if="!person.activities || person.activities.length === 0" class="alert alert-info">
              <i class="bi bi-info-circle"></i> Aucune activité enregistrée pour cette personne.
            </div>
            
            <div v-else class="timeline">
              <div 
                v-for="activity in sortedActivities" 
                :key="activity.id" 
                class="timeline-item"
              >
                <div class="timeline-marker" :class="getNatureClass(activity.nature)"></div>
                <div class="timeline-content card mb-3">
                  <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                      <div class="flex-grow-1">
                        <h5 class="card-title">
                          <span class="badge" :class="getNatureBadgeClass(activity.nature)">
                            {{ getNatureLabel(activity.nature) }}
                          </span>
                          {{ activity.title }}
                        </h5>
                        <p class="text-muted mb-2">
                          <i class="bi bi-calendar3"></i> {{ activity.activityYear }}
                        </p>
                        <p class="card-text" v-if="activity.description">
                          {{ activity.description }}
                        </p>
                        <div v-if="activity.webAddress" class="mt-2">
                          <a :href="activity.webAddress" target="_blank" rel="noopener noreferrer" class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-link-45deg"></i> Voir en ligne
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      person: null,
      loading: true,
      error: null,
      personId: null
    };
  },
  computed: {
    sortedActivities() {
      if (!this.person || !this.person.activities) return [];
      return [...this.person.activities].sort((a, b) => b.activityYear - a.activityYear);
    }
  },
  mounted() {
    this.extractIdFromUrl();
    if (this.personId) {
      this.loadPerson();
    } else {
      this.error = 'ID de personne manquant dans l\'URL';
      this.loading = false;
    }
  },
  methods: {
    extractIdFromUrl() {
      const hash = window.location.hash;
      const match = hash.match(/\/person\/(\d+)/);
      if (match) {
        this.personId = parseInt(match[1]);
      }
    },
    
    loadPerson() {
      this.loading = true;
      this.error = null;
      
      axios.get(`/api/persons/${this.personId}`)
        .then(response => {
          this.person = response.data;
          this.loading = false;
        })
        .catch(err => {
          console.error('Erreur:', err);
          this.error = 'Impossible de charger ce CV';
          this.loading = false;
        });
    },
    
    goBack() {
      window.history.back();
    },
    
    getInitials(firstName, lastName) {
      return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('fr-FR', { year: 'numeric', month: 'long', day: 'numeric' });
    },
    
    getTotalActivities() {
      return this.person && this.person.activities ? this.person.activities.length : 0;
    },
    
    getActivitiesByNature(nature) {
      if (!this.person || !this.person.activities) return 0;
      return this.person.activities.filter(a => a.nature === nature).length;
    },
    
    getNatureLabel(nature) {
      const labels = {
        'EXPERIENCE_PROFESSIONAL': '💼 Expérience Pro',
        'TRAINING': '🎓 Formation',
        'PROJECTS': '🚀 Projet',
        'OTHER': '📌 Autre'
      };
      return labels[nature] || nature;
    },
    
    getNatureBadgeClass(nature) {
      const classes = {
        'EXPERIENCE_PROFESSIONAL': 'bg-primary',
        'TRAINING': 'bg-success',
        'PROJECTS': 'bg-warning text-dark',
        'OTHER': 'bg-secondary'
      };
      return classes[nature] || 'bg-secondary';
    },
    
    getNatureClass(nature) {
      const classes = {
        'EXPERIENCE_PROFESSIONAL': 'marker-primary',
        'TRAINING': 'marker-success',
        'PROJECTS': 'marker-warning',
        'OTHER': 'marker-secondary'
      };
      return classes[nature] || 'marker-secondary';
    }
  }
};
</script>

<style scoped>
.avatar-large {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 2rem;
}

.bg-gradient-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.stat-item {
  text-align: center;
  padding: 10px;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
}

.stat-label {
  font-size: 0.85rem;
  color: #6c757d;
}

.timeline {
  position: relative;
  padding-left: 30px;
}

.timeline-item {
  position: relative;
  margin-bottom: 20px;
}

.timeline-marker {
  position: absolute;
  left: -30px;
  top: 10px;
  width: 15px;
  height: 15px;
  border-radius: 50%;
  border: 3px solid #fff;
  box-shadow: 0 0 0 2px #667eea;
}

.marker-primary { box-shadow: 0 0 0 2px #0d6efd; }
.marker-success { box-shadow: 0 0 0 2px #198754; }
.marker-warning { box-shadow: 0 0 0 2px #ffc107; }
.marker-secondary { box-shadow: 0 0 0 2px #6c757d; }

.timeline::before {
  content: '';
  position: absolute;
  left: -23px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e9ecef;
}
</style>
