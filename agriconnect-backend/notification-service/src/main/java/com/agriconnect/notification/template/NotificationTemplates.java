package com.agriconnect.notification.template;

import com.agriconnect.notification.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

public final class NotificationTemplates {

    private NotificationTemplates() {}

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NotificationTemplate {
        private String title;
        private String body;
        private String actionUrl;
    }

    public static NotificationTemplate get(NotificationType type, Map<String, String> params) {
        return switch (type) {
            // ── KYC ──────────────────────────────────────────────────────────
            case KYC_SUBMITTED -> NotificationTemplate.builder()
                    .title("Vérification en cours ⏳")
                    .body("Votre dossier KYC a été soumis. Résultat sous 24-48h.")
                    .build();
            case KYC_APPROVED -> NotificationTemplate.builder()
                    .title("Identité vérifiée ✅")
                    .body("Félicitations ! Votre compte est maintenant entièrement vérifié. Toutes les fonctionnalités sont débloquées.")
                    .actionUrl("/profile")
                    .build();
            case KYC_REJECTED -> NotificationTemplate.builder()
                    .title("Vérification échouée ❌")
                    .body("Votre dossier KYC a été refusé. Motif : " + p(params, "reason", "document illisible") + ". Veuillez soumettre à nouveau.")
                    .actionUrl("/kyc")
                    .build();

            // ── Labor ─────────────────────────────────────────────────────────
            case JOB_APPLICATION_RECEIVED -> NotificationTemplate.builder()
                    .title("Nouvelle candidature 👤")
                    .body(p(params, "workerName", "Un travailleur") + " a candidaté à votre offre « " + p(params, "jobTitle", "votre offre") + " ».")
                    .actionUrl("/jobs/" + p(params, "jobId", "") + "/applications")
                    .build();
            case APPLICATION_ACCEPTED -> NotificationTemplate.builder()
                    .title("Candidature acceptée 🎉")
                    .body("Bonne nouvelle ! " + p(params, "farmerName", "L'agriculteur") + " a accepté votre candidature pour « " + p(params, "jobTitle", "la mission") + " ».")
                    .actionUrl("/applications/" + p(params, "applicationId", ""))
                    .build();
            case APPLICATION_REJECTED -> NotificationTemplate.builder()
                    .title("Candidature non retenue")
                    .body("Votre candidature pour « " + p(params, "jobTitle", "la mission") + " » n'a pas été retenue. D'autres opportunités vous attendent !")
                    .actionUrl("/jobs")
                    .build();
            case CONTRACT_GENERATED -> NotificationTemplate.builder()
                    .title("Contrat prêt à signer 📝")
                    .body("Un contrat de mission a été généré pour « " + p(params, "jobTitle", "la mission") + " ». Signez-le pour confirmer.")
                    .actionUrl("/contracts/" + p(params, "contractId", ""))
                    .build();
            case CONTRACT_SIGNED -> NotificationTemplate.builder()
                    .title("Contrat signé ✅")
                    .body("Le contrat pour « " + p(params, "jobTitle", "la mission") + " » est signé par les deux parties. La mission peut commencer !")
                    .actionUrl("/missions/" + p(params, "missionId", ""))
                    .build();
            case MISSION_STARTED -> NotificationTemplate.builder()
                    .title("Mission démarrée 🌱")
                    .body("La mission « " + p(params, "jobTitle", "votre mission") + " » a officiellement démarré.")
                    .actionUrl("/missions/" + p(params, "missionId", ""))
                    .build();
            case MISSION_COMPLETED -> NotificationTemplate.builder()
                    .title("Mission terminée 🎊")
                    .body("La mission est terminée et validée. Le paiement de " + p(params, "amount", "votre montant") + " FCFA a été libéré.")
                    .actionUrl("/wallet")
                    .build();
            case MISSION_DISPUTED -> NotificationTemplate.builder()
                    .title("Litige ouvert ⚠️")
                    .body("Un litige a été ouvert sur votre mission. Notre équipe va vous contacter dans les 24h.")
                    .actionUrl("/missions/" + p(params, "missionId", ""))
                    .build();

            // ── Marketplace ───────────────────────────────────────────────────
            case ORDER_CONFIRMED -> NotificationTemplate.builder()
                    .title("Commande confirmée 🛒")
                    .body("Votre commande #" + p(params, "orderId", "XXX") + " a été confirmée. Le producteur prépare vos produits.")
                    .actionUrl("/orders/" + p(params, "orderId", ""))
                    .build();
            case ORDER_SHIPPED -> NotificationTemplate.builder()
                    .title("Commande en route 🚴")
                    .body("Votre commande est en cours de livraison. Livraison estimée : " + p(params, "deliveryTime", "aujourd'hui") + ".")
                    .actionUrl("/orders/" + p(params, "orderId", ""))
                    .build();
            case ORDER_DELIVERED -> NotificationTemplate.builder()
                    .title("Commande livrée ✅")
                    .body("Votre commande a été livrée avec succès. Donnez votre avis sur les produits !")
                    .actionUrl("/orders/" + p(params, "orderId", "") + "/review")
                    .build();

            // ── Payment ───────────────────────────────────────────────────────
            case TOPUP_SUCCESS -> NotificationTemplate.builder()
                    .title("Rechargement réussi 💰")
                    .body("Votre wallet a été rechargé de " + p(params, "amount", "0") + " FCFA. Nouveau solde : " + p(params, "balance", "0") + " FCFA.")
                    .actionUrl("/wallet")
                    .build();
            case TOPUP_FAILED -> NotificationTemplate.builder()
                    .title("Échec du rechargement ❌")
                    .body("Le rechargement de " + p(params, "amount", "0") + " FCFA a échoué. Vérifiez votre solde Mobile Money et réessayez.")
                    .actionUrl("/wallet")
                    .build();
            case PAYMENT_RECEIVED -> NotificationTemplate.builder()
                    .title("Paiement reçu 💵")
                    .body("Vous avez reçu " + p(params, "amount", "0") + " FCFA sur votre wallet AgriConnect.")
                    .actionUrl("/wallet")
                    .build();
            case WITHDRAWAL_SUCCESS -> NotificationTemplate.builder()
                    .title("Retrait effectué ✅")
                    .body(p(params, "amount", "0") + " FCFA ont été envoyés vers votre " + p(params, "provider", "Mobile Money") + " (" + p(params, "phone", "***") + ").")
                    .actionUrl("/wallet")
                    .build();
            case WITHDRAWAL_FAILED -> NotificationTemplate.builder()
                    .title("Échec du retrait ❌")
                    .body("Le retrait de " + p(params, "amount", "0") + " FCFA a échoué. Votre solde a été remboursé.")
                    .actionUrl("/wallet")
                    .build();
            case ESCROW_LOCKED -> NotificationTemplate.builder()
                    .title("Paiement sécurisé 🔒")
                    .body(p(params, "amount", "0") + " FCFA ont été bloqués en séquestre pour votre mission. Ils seront libérés à la fin.")
                    .build();
            case ESCROW_RELEASED -> NotificationTemplate.builder()
                    .title("Paiement libéré 🎉")
                    .body(p(params, "amount", "0") + " FCFA ont été crédités sur votre wallet suite à la validation de la mission.")
                    .actionUrl("/wallet")
                    .build();

            // ── Account ───────────────────────────────────────────────────────
            case ACCOUNT_VERIFIED -> NotificationTemplate.builder()
                    .title("Compte activé 🎊")
                    .body("Bienvenue sur AgriConnect ! Votre numéro de téléphone a été vérifié. Commencez à explorer.")
                    .actionUrl("/home")
                    .build();
            case NEW_REVIEW_RECEIVED -> NotificationTemplate.builder()
                    .title("Nouvel avis reçu ⭐")
                    .body(p(params, "reviewerName", "Un utilisateur") + " vous a laissé un avis " + p(params, "rating", "5") + "/5 étoiles.")
                    .actionUrl("/profile")
                    .build();

            default -> NotificationTemplate.builder()
                    .title("AgriConnect")
                    .body("Vous avez une nouvelle notification.")
                    .build();
        };
    }

    private static String p(Map<String, String> params, String key, String defaultVal) {
        if (params == null) return defaultVal;
        return params.getOrDefault(key, defaultVal);
    }
}
