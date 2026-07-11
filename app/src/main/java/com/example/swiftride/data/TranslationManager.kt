package com.example.swiftride.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.concurrent.ConcurrentHashMap

enum class AppLanguage(val code: String, val displayName: String, val mlKitCode: String) {
    ENGLISH_US("en-US", "English (US)", "en"),
    ENGLISH_UK("en-GB", "English (UK)", "en"),
    DEUTSCH("de-DE", "Deutsch (GER)", "de"),
    FRENCH("fr-FR", "French (FRA)", "fr"),
    SPANISH("es-ES", "Spanish (ESP)", "es");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().find { it.code.lowercase() == code.lowercase() } ?: ENGLISH_US
        }
    }
}

object TranslationManager {

    private val translators = ConcurrentHashMap<String, Translator>()
    private val translationCache = ConcurrentHashMap<Pair<String, String>, String>()

    // Local static translation dictionary for critical UI elements to provide instant load times
    val staticDictionary = mapOf(
        "Account Profile" to mapOf(
            "en-US" to "Account Profile",
            "en-GB" to "Account Profile",
            "de-DE" to "Kontoprofil",
            "fr-FR" to "Profil de Compte",
            "es-ES" to "Perfil de la Cuenta"
        ),
        "App Settings" to mapOf(
            "en-US" to "App Settings",
            "en-GB" to "App Settings",
            "de-DE" to "App-Einstellungen",
            "fr-FR" to "Paramètres de l'App",
            "es-ES" to "Ajustes de la Aplicación"
        ),
        "Manage navigation preferences" to mapOf(
            "en-US" to "Manage navigation preferences",
            "en-GB" to "Manage navigation preferences",
            "de-DE" to "Navigationseinstellungen verwalten",
            "fr-FR" to "Gérer les préférences de navigation",
            "es-ES" to "Gestionar preferencias de navegación"
        ),
        "ACCOUNT" to mapOf(
            "en-US" to "ACCOUNT",
            "en-GB" to "ACCOUNT",
            "de-DE" to "KONTO",
            "fr-FR" to "COMPTE",
            "es-ES" to "CUENTA"
        ),
        "Active Account Session" to mapOf(
            "en-US" to "Active Account Session",
            "en-GB" to "Active Account Session",
            "de-DE" to "Aktive Kontositzung",
            "fr-FR" to "Session de Compte Active",
            "es-ES" to "Sesión de Cuenta Activa"
        ),
        "Delete Account" to mapOf(
            "en-US" to "Delete Account",
            "en-GB" to "Delete Account",
            "de-DE" to "Konto löschen",
            "fr-FR" to "Supprimer le Compte",
            "es-ES" to "Eliminar Cuenta"
        ),
        "PREFERENCES" to mapOf(
            "en-US" to "PREFERENCES",
            "en-GB" to "PREFERENCES",
            "de-DE" to "EINSTELLUNGEN",
            "fr-FR" to "PRÉFÉRENCES",
            "es-ES" to "PREFERENCIAS"
        ),
        "Light" to mapOf(
            "en-US" to "Light",
            "en-GB" to "Light",
            "de-DE" to "Hell",
            "fr-FR" to "Clair",
            "es-ES" to "Claro"
        ),
        "Dark" to mapOf(
            "en-US" to "Dark",
            "en-GB" to "Dark",
            "de-DE" to "Dunkel",
            "fr-FR" to "Sombre",
            "es-ES" to "Oscuro"
        ),
        "UI Mode" to mapOf(
            "en-US" to "UI Mode",
            "en-GB" to "UI Mode",
            "de-DE" to "UI-Modus",
            "fr-FR" to "Mode UI",
            "es-ES" to "Modo UI"
        ),
        "Dark Mode" to mapOf(
            "en-US" to "Dark Mode",
            "en-GB" to "Dark Mode",
            "de-DE" to "Dunkelmodus",
            "fr-FR" to "Mode Sombre",
            "es-ES" to "Modo Oscuro"
        ),
        "Optimize battery consumption" to mapOf(
            "en-US" to "Optimize battery consumption",
            "en-GB" to "Optimize battery consumption",
            "de-DE" to "Akkulaufzeit optimieren",
            "fr-FR" to "Optimiser la consommation de batterie",
            "es-ES" to "Optimizar el consumo de batería"
        ),
        "Map Provider" to mapOf(
            "en-US" to "Map Provider",
            "en-GB" to "Map Provider",
            "de-DE" to "Kartenanbieter",
            "fr-FR" to "Fournisseur de Carte",
            "es-ES" to "Proveedor de Mapas"
        ),
        "Selected navigation layer" to mapOf(
            "en-US" to "Selected navigation layer",
            "en-GB" to "Selected navigation layer",
            "de-DE" to "Ausgewählte Navigationsebene",
            "fr-FR" to "Couche de navigation sélectionnée",
            "es-ES" to "Capa de navegación seleccionada"
        ),
        "Language" to mapOf(
            "en-US" to "Language",
            "en-GB" to "Language",
            "de-DE" to "Sprache",
            "fr-FR" to "Langue",
            "es-ES" to "Idioma"
        ),
        "Choose your preferred language" to mapOf(
            "en-US" to "Choose your preferred language",
            "en-GB" to "Choose your preferred language",
            "de-DE" to "Wählen Sie Ihre bevorzugte Sprache",
            "fr-FR" to "Choisissez votre langue préférée",
            "es-ES" to "Elige tu idioma preferido"
        ),
        "Logout" to mapOf(
            "en-US" to "Logout",
            "en-GB" to "Logout",
            "de-DE" to "Abmelden",
            "fr-FR" to "Se Déconnecter",
            "es-ES" to "Cerrar Sesión"
        ),
        "Your Trips" to mapOf(
            "en-US" to "Your Trips",
            "en-GB" to "Your Trips",
            "de-DE" to "Ihre Fahrten",
            "fr-FR" to "Vos Voyages",
            "es-ES" to "Tus Viajes"
        ),
        "View history and trip receipts" to mapOf(
            "en-US" to "View history and trip receipts",
            "en-GB" to "View history and trip receipts",
            "de-DE" to "Verlauf und Belege anzeigen",
            "fr-FR" to "Voir l'historique et les reçus",
            "es-ES" to "Ver historial y recibos de viaje"
        ),
        "Wallet & Payment" to mapOf(
            "en-US" to "Wallet & Payment",
            "en-GB" to "Wallet & Payment",
            "de-DE" to "Brieftasche & Zahlung",
            "fr-FR" to "Portefeuille & Paiement",
            "es-ES" to "Billetera y Pago"
        ),
        "Manage credit cards & payment options" to mapOf(
            "en-US" to "Manage credit cards & payment options",
            "en-GB" to "Manage credit cards & payment options",
            "de-DE" to "Kreditkarten & Zahlungsoptionen verwalten",
            "fr-FR" to "Gérer les cartes de crédit et paiements",
            "es-ES" to "Administrar tarjetas de crédito y pagos"
        ),
        "Privacy & Security" to mapOf(
            "en-US" to "Privacy & Security",
            "en-GB" to "Privacy & Security",
            "de-DE" to "Datenschutz & Sicherheit",
            "fr-FR" to "Confidentialité & Sécurité",
            "es-ES" to "Privacidad y Seguridad"
        ),
        "Configure two-step verification" to mapOf(
            "en-US" to "Configure two-step verification",
            "en-GB" to "Configure two-step verification",
            "de-DE" to "Zweistufige Verifizierung einrichten",
            "fr-FR" to "Configurer la vérification en deux étapes",
            "es-ES" to "Configurar verificación en dos pasos"
        ),
        "Help Center" to mapOf(
            "en-US" to "Help Center",
            "en-GB" to "Help Center",
            "de-DE" to "Hilfezentrum",
            "fr-FR" to "Centre d'Aide",
            "es-ES" to "Centro de Ayuda"
        ),
        "Access client support and articles" to mapOf(
            "en-US" to "Access client support and articles",
            "en-GB" to "Access client support and articles",
            "de-DE" to "Kundenservice und Artikel aufrufen",
            "fr-FR" to "Accéder au support client et articles",
            "es-ES" to "Acceder a soporte al cliente y artículos"
        ),
        "Home" to mapOf(
            "en-US" to "Home",
            "en-GB" to "Home",
            "de-DE" to "Startseite",
            "fr-FR" to "Accueil",
            "es-ES" to "Inicio"
        ),
        "Ride" to mapOf(
            "en-US" to "Ride",
            "en-GB" to "Ride",
            "de-DE" to "Fahrt",
            "fr-FR" to "Course",
            "es-ES" to "Viaje"
        ),
        "Services" to mapOf(
            "en-US" to "Services",
            "en-GB" to "Services",
            "de-DE" to "Dienstleistungen",
            "fr-FR" to "Services",
            "es-ES" to "Servicios"
        ),
        "Activity" to mapOf(
            "en-US" to "Activity",
            "en-GB" to "Activity",
            "de-DE" to "Aktivität",
            "fr-FR" to "Activité",
            "es-ES" to "Actividad"
        ),
        "Account" to mapOf(
            "en-US" to "Account",
            "en-GB" to "Account",
            "de-DE" to "Konto",
            "fr-FR" to "Compte",
            "es-ES" to "Cuenta"
        ),
        "For you" to mapOf(
            "en-US" to "For you",
            "en-GB" to "For you",
            "de-DE" to "Für dich",
            "fr-FR" to "Pour vous",
            "es-ES" to "Para ti"
        ),
        "Where to?" to mapOf(
            "en-US" to "Where to?",
            "en-GB" to "Where to?",
            "de-DE" to "Wohin?",
            "fr-FR" to "Où allez-vous ?",
            "es-ES" to "¿Adónde va?"
        ),
        "Cancel" to mapOf(
            "en-US" to "Cancel",
            "en-GB" to "Cancel",
            "de-DE" to "Abbrechen",
            "fr-FR" to "Annuler",
            "es-ES" to "Cancelar"
        ),
        "Confirm" to mapOf(
            "en-US" to "Confirm",
            "en-GB" to "Confirm",
            "de-DE" to "Bestätigen",
            "fr-FR" to "Confirmer",
            "es-ES" to "Confirmar"
        ),
        "Confirm Route" to mapOf(
            "en-US" to "Confirm Route",
            "en-GB" to "Confirm Route",
            "de-DE" to "Route bestätigen",
            "fr-FR" to "Confirmer l'itinéraire",
            "es-ES" to "Confirmar ruta"
        ),
        "Delete Account?" to mapOf(
            "en-US" to "Delete Account?",
            "en-GB" to "Delete Account?",
            "de-DE" to "Konto löschen?",
            "fr-FR" to "Supprimer le compte ?",
            "es-ES" to "¿Eliminar cuenta?"
        ),
        "Done" to mapOf(
            "en-US" to "Done",
            "en-GB" to "Done",
            "de-DE" to "Fertig",
            "fr-FR" to "Terminé",
            "es-ES" to "Hecho"
        ),
        "Booking Confirmed!" to mapOf(
            "en-US" to "Booking Confirmed!",
            "en-GB" to "Booking Confirmed!",
            "de-DE" to "Buchung bestätigt!",
            "fr-FR" to "Réservation confirmée !",
            "es-ES" to "¡Reserva confirmada!"
        ),
        "Suggestions" to mapOf(
            "en-US" to "Suggestions",
            "en-GB" to "Suggestions",
            "de-DE" to "Vorschläge",
            "fr-FR" to "Suggestions",
            "es-ES" to "Sugerencias"
        ),
        "Auto" to mapOf(
            "en-US" to "Auto",
            "en-GB" to "Auto",
            "de-DE" to "Auto",
            "fr-FR" to "Auto",
            "es-ES" to "Auto"
        ),
        "Affordable local rickshaw rides" to mapOf(
            "en-US" to "Affordable local rickshaw rides",
            "en-GB" to "Affordable local rickshaw rides",
            "de-DE" to "Günstige lokale Rikscha-Fahrten",
            "fr-FR" to "Courses de rickshaw locales abordables",
            "es-ES" to "Viajes en mototaxi locales y asequibles"
        ),
        "Cab" to mapOf(
            "en-US" to "Cab",
            "en-GB" to "Cab",
            "de-DE" to "Taxi",
            "fr-FR" to "Taxi",
            "es-ES" to "Taxi"
        ),
        "Request a fast everyday ride" to mapOf(
            "en-US" to "Request a fast everyday ride",
            "en-GB" to "Request a fast everyday ride",
            "de-DE" to "Fordern Sie eine schnelle Alltagsfahrt an",
            "fr-FR" to "Demandez une course quotidienne rapide",
            "es-ES" to "Solicita un viaje diario rápido"
        ),
        "Bus & Train" to mapOf(
            "en-US" to "Bus & Train",
            "en-GB" to "Bus & Train",
            "de-DE" to "Bus & Bahn",
            "fr-FR" to "Bus & Train",
            "es-ES" to "Autobús y Tren"
        ),
        "Public transit tickets & timetables" to mapOf(
            "en-US" to "Public transit tickets & timetables",
            "en-GB" to "Public transit tickets & timetables",
            "de-DE" to "Fahrkarten & Fahrpläne für den ÖPNV",
            "fr-FR" to "Billets et horaires de transport public",
            "es-ES" to "Billetes y horarios de transporte público"
        ),
        "Bike" to mapOf(
            "en-US" to "Bike",
            "en-GB" to "Bike",
            "de-DE" to "Motorrad",
            "fr-FR" to "Moto",
            "es-ES" to "Moto"
        ),
        "Fast and economic bike rides" to mapOf(
            "en-US" to "Fast and economic bike rides",
            "en-GB" to "Fast and economic bike rides",
            "de-DE" to "Schnelle und wirtschaftliche Motorradfahrten",
            "fr-FR" to "Courses de moto rapides et économiques",
            "es-ES" to "Viajes en moto rápidos y económicos"
        ),
        "Rentals" to mapOf(
            "en-US" to "Rentals",
            "en-GB" to "Rentals",
            "de-DE" to "Mietwagen",
            "fr-FR" to "Locations",
            "es-ES" to "Alquileres"
        ),
        "Rent vehicles for self-drive" to mapOf(
            "en-US" to "Rent vehicles for self-drive",
            "en-GB" to "Rent vehicles for self-drive",
            "de-DE" to "Fahrzeuge zum Selberfahren mieten",
            "fr-FR" to "Louez des véhicules pour conduire vous-même",
            "es-ES" to "Alquila vehículos para conducir tú mismo"
        ),
        "Intercity" to mapOf(
            "en-US" to "Intercity",
            "en-GB" to "Intercity",
            "de-DE" to "Fernverkehr",
            "fr-FR" to "Interurbain",
            "es-ES" to "Interurbano"
        ),
        "Comfortable outstation travel" to mapOf(
            "en-US" to "Comfortable outstation travel",
            "en-GB" to "Comfortable outstation travel",
            "de-DE" to "Bequemes Reisen in andere Städte",
            "fr-FR" to "Voyages interurbains confortables",
            "es-ES" to "Viajes interurbanos cómodos"
        ),
        "Reserve" to mapOf(
            "en-US" to "Reserve",
            "en-GB" to "Reserve",
            "de-DE" to "Reservieren",
            "fr-FR" to "Réserver",
            "es-ES" to "Reservar"
        ),
        "Schedule your rides in advance" to mapOf(
            "en-US" to "Schedule your rides in advance",
            "en-GB" to "Schedule your rides in advance",
            "de-DE" to "Planen Sie Ihre Fahrten im Voraus",
            "fr-FR" to "Planifiez vos courses à l'avance",
            "es-ES" to "Programa tus viajes con anticipación"
        ),
        "Teens" to mapOf(
            "en-US" to "Teens",
            "en-GB" to "Teens",
            "de-DE" to "Jugendliche",
            "fr-FR" to "Ados",
            "es-ES" to "Adolescentes"
        ),
        "Monitored rides for students" to mapOf(
            "en-US" to "Monitored rides for students",
            "en-GB" to "Monitored rides for students",
            "de-DE" to "Überwachte Fahrten für Schüler",
            "fr-FR" to "Courses surveillées pour étudiants",
            "es-ES" to "Viajes supervisados para estudiantes"
        ),
        "Welcome back! Let's get riding." to mapOf(
            "en-US" to "Welcome back! Let's get riding.",
            "en-GB" to "Welcome back! Let's get riding.",
            "de-DE" to "Willkommen zurück! Gute Fahrt.",
            "fr-FR" to "Bon retour ! En route.",
            "es-ES" to "¡Bienvenido de nuevo! A viajar."
        ),
        "Create your account to get started." to mapOf(
            "en-US" to "Create your account to get started.",
            "en-GB" to "Create your account to get started.",
            "de-DE" to "Erstellen Sie Ihr Konto, um zu starten.",
            "fr-FR" to "Créez votre compte pour commencer.",
            "es-ES" to "Crea tu cuenta para empezar."
        ),
        "Full Name" to mapOf(
            "en-US" to "Full Name",
            "en-GB" to "Full Name",
            "de-DE" to "Vollständiger Name",
            "fr-FR" to "Nom complet",
            "es-ES" to "Nombre completo"
        ),
        "Enter your name" to mapOf(
            "en-US" to "Enter your name",
            "en-GB" to "Enter your name",
            "de-DE" to "Name eingeben",
            "fr-FR" to "Entrez votre nom",
            "es-ES" to "Introduce tu nombre"
        ),
        "Email or Phone Number" to mapOf(
            "en-US" to "Email or Phone Number",
            "en-GB" to "Email or Phone Number",
            "de-DE" to "E-Mail oder Telefonnummer",
            "fr-FR" to "E-mail ou numéro de téléphone",
            "es-ES" to "Correo o número de teléfono"
        ),
        "Password" to mapOf(
            "en-US" to "Password",
            "en-GB" to "Password",
            "de-DE" to "Passwort",
            "fr-FR" to "Mot de passe",
            "es-ES" to "Contraseña"
        ),
        "Min. 8 characters" to mapOf(
            "en-US" to "Min. 8 characters",
            "en-GB" to "Min. 8 characters",
            "de-DE" to "Mindest. 8 Zeichen",
            "fr-FR" to "Min. 8 caractères",
            "es-ES" to "Mín. 8 caracteres"
        ),
        "HIDE" to mapOf(
            "en-US" to "HIDE",
            "en-GB" to "HIDE",
            "de-DE" to "AUSBLENDEN",
            "fr-FR" to "MASQUER",
            "es-ES" to "OCULTAR"
        ),
        "SHOW" to mapOf(
            "en-US" to "SHOW",
            "en-GB" to "SHOW",
            "de-DE" to "ANZEIGEN",
            "fr-FR" to "AFFICHER",
            "es-ES" to "MOSTRAR"
        ),
        "Forgot password?" to mapOf(
            "en-US" to "Forgot password?",
            "en-GB" to "Forgot password?",
            "de-DE" to "Passwort vergessen?",
            "fr-FR" to "Mot de passe oublié ?",
            "es-ES" to "¿Olvidaste tu contraseña?"
        ),
        "Invalid email/phone or password" to mapOf(
            "en-US" to "Invalid email/phone or password",
            "en-GB" to "Invalid email/phone or password",
            "de-DE" to "Ungültige E-Mail/Telefonnummer oder Passwort",
            "fr-FR" to "E-mail/téléphone ou mot de passe invalide",
            "es-ES" to "Correo/teléfono o contraseña no válidos"
        ),
        "Please fill in all fields" to mapOf(
            "en-US" to "Please fill in all fields",
            "en-GB" to "Please fill in all fields",
            "de-DE" to "Bitte füllen Sie alle Felder aus",
            "fr-FR" to "Veuillez remplir tous les champs",
            "es-ES" to "Por favor, rellene todos los campos"
        ),
        "Password must be at least 4 characters" to mapOf(
            "en-US" to "Password must be at least 4 characters",
            "en-GB" to "Password must be at least 4 characters",
            "de-DE" to "Passwort muss mindestens 4 Zeichen lang sein",
            "fr-FR" to "Le mot de passe doit comporter au moins 4 caractères",
            "es-ES" to "La contraseña debe tener al menos 4 caracteres"
        ),
        "User already exists" to mapOf(
            "en-US" to "User already exists",
            "en-GB" to "User already exists",
            "de-DE" to "Benutzer existiert bereits",
            "fr-FR" to "L'utilisateur existe déjà",
            "es-ES" to "El usuario ya existe"
        ),
        "Continue" to mapOf(
            "en-US" to "Continue",
            "en-GB" to "Continue",
            "de-DE" to "Weiter",
            "fr-FR" to "Continuer",
            "es-ES" to "Continuar"
        ),
        "Create Account" to mapOf(
            "en-US" to "Create Account",
            "en-GB" to "Create Account",
            "de-DE" to "Konto erstellen",
            "fr-FR" to "Créer un compte",
            "es-ES" to "Crear cuenta"
        ),
        "or" to mapOf(
            "en-US" to "or",
            "en-GB" to "or",
            "de-DE" to "oder",
            "fr-FR" to "ou",
            "es-ES" to "o"
        ),
        "Google" to mapOf(
            "en-US" to "Google",
            "en-GB" to "Google",
            "de-DE" to "Google",
            "fr-FR" to "Google",
            "es-ES" to "Google"
        ),
        "Apple" to mapOf(
            "en-US" to "Apple",
            "en-GB" to "Apple",
            "de-DE" to "Apple",
            "fr-FR" to "Apple",
            "es-ES" to "Apple"
        ),
        "New to SwiftRide? " to mapOf(
            "en-US" to "New to SwiftRide? ",
            "en-GB" to "New to SwiftRide? ",
            "de-DE" to "Neu bei SwiftRide? ",
            "fr-FR" to "Nouveau sur SwiftRide ? ",
            "es-ES" to "¿Nuevo en SwiftRide? "
        ),
        "Already have an account? " to mapOf(
            "en-US" to "Already have an account? ",
            "en-GB" to "Already have an account? ",
            "de-DE" to "Haben Sie bereits ein Konto? ",
            "fr-FR" to "Vous avez déjà un compte ? ",
            "es-ES" to "¿Ya tienes una cuenta? "
        ),
        "Sign Up" to mapOf(
            "en-US" to "Sign Up",
            "en-GB" to "Sign Up",
            "de-DE" to "Registrieren",
            "fr-FR" to "S'inscrire",
            "es-ES" to "Registrarse"
        ),
        "Log In" to mapOf(
            "en-US" to "Log In",
            "en-GB" to "Log In",
            "de-DE" to "Anmelden",
            "fr-FR" to "Se connecter",
            "es-ES" to "Iniciar sesión"
        ),
        "Your Journey begins with us" to mapOf(
            "en-US" to "Your Journey begins with us",
            "en-GB" to "Your Journey begins with us",
            "de-DE" to "Ihre Reise beginnt mit uns",
            "fr-FR" to "Votre voyage commence avec nous",
            "es-ES" to "Tu viaje comienza con nosotros"
        ),
        "Distance cannot exceed 150 km." to mapOf(
            "en-US" to "Distance cannot exceed 150 km.",
            "en-GB" to "Distance cannot exceed 150 km.",
            "de-DE" to "Entfernung darf 150 km nicht überschreiten.",
            "fr-FR" to "La distance ne peut pas dépasser 150 km.",
            "es-ES" to "La distancia no puede superar los 150 km."
        ),
        "Service is only available within Bengaluru, India." to mapOf(
            "en-US" to "Service is only available within Bengaluru, India.",
            "en-GB" to "Service is only available within Bengaluru, India.",
            "de-DE" to "Der Service ist nur in Bengaluru, Indien, verfügbar.",
            "fr-FR" to "Le service est uniquement disponible à Bengaluru, en Inde.",
            "es-ES" to "El servicio solo está disponible en Bengaluru, India."
        ),
        "Please enter both locations." to mapOf(
            "en-US" to "Please enter both locations.",
            "en-GB" to "Please enter both locations.",
            "de-DE" to "Bitte geben Sie beide Orte ein.",
            "fr-FR" to "Veuillez saisir les deux emplacements.",
            "es-ES" to "Por favor, introduzca ambas ubicaciones."
        ),
        "Current Location (Bengaluru Only)" to mapOf(
            "en-US" to "Current Location (Bengaluru Only)",
            "en-GB" to "Current Location (Bengaluru Only)",
            "de-DE" to "Aktueller Standort (nur Bengaluru)",
            "fr-FR" to "Position actuelle (uniquement Bengaluru)",
            "es-ES" to "Ubicación actual (solo Bengaluru)"
        ),
        "Where to? (Bengaluru Only)" to mapOf(
            "en-US" to "Where to? (Bengaluru Only)",
            "en-GB" to "Where to? (Bengaluru Only)",
            "de-DE" to "Wohin? (nur Bengaluru)",
            "fr-FR" to "Où allez-vous ? (uniquement Bengaluru)",
            "es-ES" to "¿Adónde va? (solo Bengaluru)"
        ),
        "Pickup Location" to mapOf(
            "en-US" to "Pickup Location",
            "en-GB" to "Pickup Location",
            "de-DE" to "Abholort",
            "fr-FR" to "Lieu de prise en charge",
            "es-ES" to "Lugar de recogida"
        ),
        "Destination Location" to mapOf(
            "en-US" to "Destination Location",
            "en-GB" to "Destination Location",
            "de-DE" to "Zielort",
            "fr-FR" to "Destination",
            "es-ES" to "Destino"
        )
    )

    // Translates a string either from static dictionary or asynchronously from ML Kit
    fun translate(text: String, targetLanguageCode: String, onComplete: (String) -> Unit) {
        if (targetLanguageCode.equals("en-US", ignoreCase = true)) {
            onComplete(text)
            return
        }

        // 1. Check static dictionary
        val staticTranslation = staticDictionary[text]?.get(targetLanguageCode)
        if (staticTranslation != null) {
            onComplete(staticTranslation)
            return
        }

        // 2. Check memory cache
        val cacheKey = Pair(text, targetLanguageCode)
        val cached = translationCache[cacheKey]
        if (cached != null) {
            onComplete(cached)
            return
        }

        // 3. Fallback to Google ML Kit Translate
        val appLanguage = AppLanguage.fromCode(targetLanguageCode)
        val targetMlCode = appLanguage.mlKitCode
        
        try {
            val translator = getOrCreateTranslator(targetMlCode)
            val conditions = DownloadConditions.Builder()
                .build() // Download dynamically over wifi or cellular
                
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    translator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            translationCache[cacheKey] = translatedText
                            onComplete(translatedText)
                        }
                        .addOnFailureListener {
                            onComplete(text) // fallback
                        }
                }
                .addOnFailureListener {
                    onComplete(text) // fallback
                }
        } catch (e: Exception) {
            onComplete(text)
        }
    }

    fun getCachedTranslation(text: String, targetLanguageCode: String): String? {
        if (targetLanguageCode.equals("en-US", ignoreCase = true)) {
            return text
        }
        val staticTranslation = staticDictionary[text]?.get(targetLanguageCode)
        if (staticTranslation != null) return staticTranslation
        return translationCache[Pair(text, targetLanguageCode)]
    }

    private fun getOrCreateTranslator(targetMlCode: String): Translator {
        val key = "en-$targetMlCode"
        return translators.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetMlCode)!!)
                .build()
            Translation.getClient(options)
        }
    }
}

@Composable
fun String.translate(targetLanguageCode: String): String {
    val cached = TranslationManager.getCachedTranslation(this, targetLanguageCode)
    if (cached != null) {
        return cached
    }

    var translatedText by remember(this, targetLanguageCode) { mutableStateOf(this) }
    LaunchedEffect(this, targetLanguageCode) {
        TranslationManager.translate(this@translate, targetLanguageCode) {
            translatedText = it
        }
    }
    return translatedText
}
