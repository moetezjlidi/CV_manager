package com.architecture.gestion_de_cvs.config;

import com.architecture.gestion_de_cvs.model.Activity;
import com.architecture.gestion_de_cvs.model.NatureActivity;
import com.architecture.gestion_de_cvs.model.Person;
import com.architecture.gestion_de_cvs.repository.PersonRepository;
import com.architecture.gestion_de_cvs.repository.XUserRepository;
import com.architecture.gestion_de_cvs.security.XUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

        protected final Log logger = LogFactory.getLog(getClass());

        private final PersonRepository personRepository;
        private final XUserRepository xUserRepository;
        private final PasswordEncoder passwordEncoder;
        private final Random random = new Random();
        private final String[] jobTitles = { "Développeur Full Stack", "Chef de Projet IT", "Ingénieur Logiciel",
                        "Data Scientist", "Architecte Solutions", "DevOps Engineer", "Product Owner",
                        "Consultant IT", "Analyste Business", "Scrum Master" };
        private final String[] companies = { "Accenture", "Capgemini", "Sopra Steria", "Atos", "Orange", "Thales",
                        "Airbus", "Renault", "PSA", "Total", "BNP Paribas", "Société Générale",
                        "AXA", "Decathlon", "Carrefour", "LVMH", "L'Oréal", "Danone", "Schneider Electric", "EDF" };
        private final String[] schools = { "Université Paris-Saclay", "Sorbonne Université", "École Polytechnique",
                        "Centrale Paris", "ENSAM", "INSA Lyon", "UTC Compiègne", "Université de Lille" };
        private final String[] skills = { "Java", "Python", "JavaScript", "React", "Angular", "Spring Boot", "Docker",
                        "Kubernetes", "AWS", "Azure", "Machine Learning", "SQL", "MongoDB", "Git" };
        private final String[] projects = { "Migration vers le Cloud", "Refonte du SI",
                        "Développement Application Mobile",
                        "Mise en place DevOps", "Projet Big Data", "Plateforme e-commerce" };
        private final String[] firstNames = { "Jean", "Marie", "Pierre", "Sophie", "Luc", "Émilie", "Thomas", "Julie",
                        "Nicolas", "Laura", "Alexandre", "Camille", "Maxime", "Sarah", "Antoine",
                        "Léa", "François", "Chloé", "David", "Emma", "Lucas", "Inès", "Hugo", "Manon", "Nathan" };
        private final String[] lastNames = { "Dupont", "Martin", "Bernard", "Dubois", "Thomas", "Robert", "Richard",
                        "Petit", "Durand", "Leroy", "Moreau", "Simon", "Laurent", "Lefebvre",
                        "Michel", "Garcia", "David", "Bertrand", "Roux", "Vincent", "Blanc", "Girard" };

        public DataInitializer(PersonRepository personRepository, XUserRepository xUserRepository,
                        PasswordEncoder passwordEncoder) {
                this.personRepository = personRepository;
                this.xUserRepository = xUserRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(String... args) {
                logger.info("+++ DataInitializer - Initializing test data");

                if (personRepository.count() > 0) {
                        logger.info("Base de données déjà alimentée - " + personRepository.count()
                                        + " personnes existantes");
                        return;
                }

                XUser[] users = createTestUsers();
                createTestUsersWithCV(users[0], users[1]);
                generateCVsWithoutAccounts();
                logInitializationSummary();
        }

        private XUser[] createTestUsers() {
                XUser userAaa = new XUser("aaa", passwordEncoder.encode("aaa"), Set.of("ADMIN", "USER"));
                XUser userBbb = new XUser("bbb", passwordEncoder.encode("bbb"), Set.of("USER"));

                xUserRepository.save(userAaa);
                xUserRepository.save(userBbb);

                logger.info("Utilisateurs de test créés: aaa (ADMIN, USER) et bbb (USER)");
                return new XUser[] { userAaa, userBbb };
        }

        private void createTestUsersWithCV(XUser userAaa, XUser userBbb) {
                Person alice = new Person();
                alice.setFirstName("Alice");
                alice.setLastName("Anderson");
                alice.setEmail("alice.anderson@email.com");
                alice.setWebsite("https://www.alice-anderson.com");
                alice.setBirthDate(LocalDate.of(1985, 3, 15));
                alice.setUser(userAaa);
                addRandomActivities(alice);
                personRepository.save(alice);
                logger.info("CV créé pour l'utilisateur AAA (Alice Anderson)");

                Person bob = new Person();
                bob.setFirstName("Bob");
                bob.setLastName("Brown");
                bob.setEmail("bob.brown@email.com");
                bob.setWebsite("https://www.bob-brown.com");
                bob.setBirthDate(LocalDate.of(1990, 7, 22));
                bob.setUser(userBbb);
                addRandomActivities(bob);
                personRepository.save(bob);
                logger.info("CV créé pour l'utilisateur BBB (Bob Brown)");
        }

        private void generateCVsWithoutAccounts() {
                logger.info("Début de la génération de CVs supplémentaires (cooptations en attente)...");

                int numberOfPersons = 100_000;

                for (int i = 0; i < numberOfPersons; i++) {
                        String firstName = firstNames[random.nextInt(firstNames.length)];
                        String lastName = lastNames[random.nextInt(lastNames.length)];

                        Person person = new Person();
                        person.setFirstName(firstName);
                        person.setLastName(lastName);
                        person.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@email.com");
                        person.setWebsite("https://www." + firstName.toLowerCase() + lastName.toLowerCase() + ".com");
                        person.setBirthDate(LocalDate.of(1970 + random.nextInt(35), 1 + random.nextInt(12),
                                        1 + random.nextInt(28)));

                        addRandomActivities(person);
                        personRepository.save(person);

                        if ((i + 1) % 10_000 == 0) {
                                logger.info("Progression: " + (i + 1) + " / " + numberOfPersons + " CVs générés");
                        }
                }
        }

        private void logInitializationSummary() {
                logger.info("=== Initialisation terminée ===");
                logger.info("Total Personnes: " + personRepository.count());
                logger.info("  - Avec compte utilisateur: 2 (aaa, bbb)");
                logger.info("  - Cooptations en attente: " + (personRepository.count() - 2));
        }

        private void addRandomActivities(Person person) {
                for (int j = 0; j < 5; j++) {
                        Activity activity = new Activity();
                        activity.setActivityYear(2000 + random.nextInt(25));
                        activity.setPerson(person);

                        NatureActivity nature = NatureActivity.values()[random.nextInt(NatureActivity.values().length)];
                        activity.setNature(nature);

                        switch (nature) {
                                case EXPERIENCE_PROFESSIONAL -> {
                                        String company = companies[random.nextInt(companies.length)];
                                        activity.setTitle(jobTitles[random.nextInt(jobTitles.length)] + " chez "
                                                        + company);
                                        activity.setDescription("Responsable de "
                                                        + projects[random.nextInt(projects.length)] +
                                                        ". Technologies: " + skills[random.nextInt(skills.length)]);
                                        activity.setWeb("https://www." + company.toLowerCase().replace(" ", "")
                                                        + ".com");
                                }
                                case TRAINING -> {
                                        activity.setTitle("Formation en " + skills[random.nextInt(skills.length)]
                                                        + " - " +
                                                        schools[random.nextInt(schools.length)]);
                                        activity.setDescription(
                                                        "Formation sur " + skills[random.nextInt(skills.length)]);
                                        activity.setWeb("https://www.formation.fr");
                                }
                                case PROJECTS -> {
                                        String project = projects[random.nextInt(projects.length)];
                                        activity.setTitle(project);
                                        activity.setDescription(
                                                        "Projet utilisant " + skills[random.nextInt(skills.length)] +
                                                                        " et " + skills[random.nextInt(skills.length)]);
                                        activity.setWeb("https://github.com/user" + random.nextInt(1000) + "/"
                                                        + project.toLowerCase().replace(" ", "-"));
                                }
                                default -> {
                                        activity.setTitle("Certification " + skills[random.nextInt(skills.length)]);
                                        activity.setDescription("Certification professionnelle");
                                        activity.setWeb("https://www.certification.com");
                                }
                        }

                        person.getActivities().add(activity);
                }
        }
}