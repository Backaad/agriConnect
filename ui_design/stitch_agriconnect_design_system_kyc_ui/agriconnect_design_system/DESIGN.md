---
name: AgriConnect Design System
colors:
  surface: '#FFFFFF'
  surface-dim: '#d8dadf'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f3f9'
  surface-container: '#eceef3'
  surface-container-high: '#e6e8ed'
  surface-container-highest: '#e1e2e8'
  on-surface: '#191c20'
  on-surface-variant: '#414750'
  inverse-surface: '#2e3135'
  inverse-on-surface: '#eff0f6'
  outline: '#717881'
  outline-variant: '#c0c7d1'
  surface-tint: '#02629e'
  primary: '#005387'
  on-primary: '#ffffff'
  primary-container: '#1b6ca8'
  on-primary-container: '#d9e9ff'
  inverse-primary: '#9acbff'
  secondary: '#006d3d'
  on-secondary: '#ffffff'
  secondary-container: '#97f3b5'
  on-secondary-container: '#047240'
  tertiary: '#734600'
  on-tertiary: '#ffffff'
  tertiary-container: '#945b00'
  on-tertiary-container: '#ffe3c8'
  error: '#E74C3C'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#cfe5ff'
  primary-fixed-dim: '#9acbff'
  on-primary-fixed: '#001d34'
  on-primary-fixed-variant: '#004a78'
  secondary-fixed: '#9af6b8'
  secondary-fixed-dim: '#7ed99e'
  on-secondary-fixed: '#00210f'
  on-secondary-fixed-variant: '#00522d'
  tertiary-fixed: '#ffddba'
  tertiary-fixed-dim: '#ffb865'
  on-tertiary-fixed: '#2b1700'
  on-tertiary-fixed-variant: '#673d00'
  background: '#F5F7FA'
  on-background: '#191c20'
  surface-variant: '#e1e2e8'
  text-main: '#1A1A2E'
  text-muted: '#7F8C9A'
  border: '#E0E7EF'
  success: '#27AE60'
  warning: '#F39C12'
  success-light: '#EBF7EF'
  warning-light: '#FEF4E7'
typography:
  h1:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '800'
    lineHeight: '1.2'
    letterSpacing: -0.3px
  h2:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '700'
    lineHeight: '1.3'
    letterSpacing: -0.3px
  h3:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: '1.4'
  body-main:
    fontFamily: Be Vietnam Pro
    fontSize: 15px
    fontWeight: '400'
    lineHeight: '1.6'
  body-secondary:
    fontFamily: Be Vietnam Pro
    fontSize: 13px
    fontWeight: '400'
    lineHeight: '1.6'
  button-label:
    fontFamily: Be Vietnam Pro
    fontSize: 15px
    fontWeight: '700'
    lineHeight: '1'
  price-display:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '700'
    lineHeight: '1'
  code-mono:
    fontFamily: monospace
    fontSize: 13px
    fontWeight: '400'
    lineHeight: '1'
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  2xl: 48px
  3xl: 64px
  screen-edge: 20px
  section-v: 24px
  card-gap: 12px
  form-gap: 16px
---

# AgriConnect — DESIGN SYSTEM
> Référence visuelle complète · Version 1.0 · Avril 2026

---

## 🎨 Couleurs

```
Primaire       #1B6CA8   Bleu agricole — confiance, eau, ciel
Secondaire     #2E8B57   Vert forêt — nature, croissance
Accent         #E8941A   Orange soleil — énergie, chaleur
Fond           #F5F7FA   Gris très clair — background général
Surface        #FFFFFF   Blanc — cartes, modals
Texte          #1A1A2E   Quasi-noir — body text
Texte doux     #7F8C9A   Gris — placeholders, labels secondaires
Bordure        #E0E7EF   Gris clair — séparateurs, inputs
Succès         #27AE60   Vert — validations, confirmations
Erreur         #E74C3C   Rouge — erreurs, alertes destructives
Warning        #F39C12   Jaune — avertissements
```

---

## 🔤 Typographie

| Rôle | Police | Taille | Poids | Couleur |
|------|--------|--------|-------|---------|
| H1 — Titre écran | Montserrat | 28px | ExtraBold 800 | #1B6CA8 |
| H2 — Titre section | Montserrat | 22px | Bold 700 | #1A1A2E |
| H3 — Titre carte | Montserrat | 18px | SemiBold 600 | #1A1A2E |
| Corps | Nunito | 15px | Regular 400 | #1A1A2E |
| Corps secondaire | Nunito | 13px | Regular 400 | #7F8C9A |
| Label / Bouton | Nunito | 15px | Bold 700 | #FFFFFF |
| Prix / Montant | Montserrat | variable | Bold 700 | #1B6CA8 |
| Référence / Code | Courier New | 13px | Regular | #1E1E2E |

- **Line-height corps :** 1.6
- **Letter-spacing titres :** -0.3px
- **Toujours en français.** Montants en **FCFA**.

---

## 📐 Espacement & Grille

```
Base unit : 4px
xs   4px
sm   8px
md   16px
lg   24px
xl   32px
2xl  48px
3xl  64px

Padding horizontal écran : 20px
Padding vertical section  : 24px
Gap entre cartes          : 12px
Gap entre champs form     : 16px
```

---

## 🧩 Composants

### Boutons

```
Primaire     bg #1B6CA8  · text blanc  · radius 12px · height 52px · shadow légère
Secondaire   bg transparent · border 2px #1B6CA8 · text #1B6CA8
Accent/CTA   bg #E8941A  · text blanc  · radius 12px · height 52px
Succès       bg #27AE60  · text blanc
Destructif   bg #E74C3C  · text blanc
Désactivé    bg #E0E7EF  · text #7F8C9A · no shadow
Ghost        bg transparent · text #1B6CA8 · no border

États : scale(0.97) au tap + feedback haptique léger
```

### Inputs

```
Fond          #FFFFFF
Border        1.5px solid #E0E7EF
Border-radius 10px
Height        52px
Padding       0 16px
Font          Nunito 15px #1A1A2E

Focus → border 2px #1B6CA8 + box-shadow 0 0 0 3px rgba(27,108,168,0.15)
Erreur → border 2px #E74C3C + shake animation + message rouge inline
Succès → border 2px #27AE60 + icône ✓ à droite
```

### Cartes (Cards)

```
Fond          #FFFFFF
Border-radius 16px
Shadow        0 4px 16px rgba(0,0,0,0.08)
Padding       16px
Hover/Press   shadow 0 8px 24px rgba(0,0,0,0.12) + scale(1.01)
```

### Badges & Pills

```
Border-radius 100px (full pill)
Padding       4px 12px
Font          Nunito Bold 12px

KYC Vérifié  bg #EBF7EF · text #2E8B57 · icône ✅
KYC En cours bg #FEF4E7 · text #E8941A · icône ⚠️
En ligne     bg #27AE60 · 8px dot · text blanc
Hors ligne   bg #7F8C9A · 8px dot · text blanc
Nouveau      bg #E74C3C · text blanc
```

### Avatar

```
Forme        cercle
Taille std   48px (profil) / 36px (liste) / 90px (page profil)
Border       2px selon statut KYC :
             · Vérifié   → 2px #27AE60
             · En cours  → 2px #E8941A
             · Non vérifié → 2px #E0E7EF
Fallback     initiales sur fond dégradé bleu→vert
```

---

## 📱 Navigation

### Bottom Navigation Bar

```
Height        64px
Fond          #FFFFFF
Shadow        0 -2px 16px rgba(0,0,0,0.08)

Onglets (5) :
  1. 🏠 Accueil
  2. 💼 Missions
  3. [+] FAB central — cercle #E8941A, Ø56px, surélevé +8px, icône + blanc
  4. 🛒 Marché
  5. 👤 Moi

Actif   icône #1B6CA8 + label Nunito Bold + dot orange 4px en bas
Inactif icône #7F8C9A + label Nunito Regular
Badges  cercle rouge 16px · Nunito Bold 10px blanc
```

### Top App Bar

```
Height 56px · Fond #FFFFFF

Type A (Home/Listes)  Avatar + Bonjour [Prénom] 👋 | Icône cloche + recherche
Type B (Détail)       ← retour | Titre centré | Action contextuelle
Type C (Carte)        Transparent flottant · bouton retour fond blanc circulaire
Type D (Formulaire)   Annuler (texte) | Titre | Enregistrer (texte bleu)
```

---

## 🗺️ Architecture des écrans

```
ONBOARDING
  └── Splash Screen
  └── Slides 1/2/3
  └── Choix du rôle

AUTH
  └── Saisie téléphone
  └── Vérification OTP
  └── KYC Étape 1 — Infos personnelles
  └── KYC Étape 2 — Document identité
  └── KYC Étape 3 — Selfie liveness
  └── KYC Étape 4 — Récapitulatif

HOME (3 variantes selon rôle)
  └── Agriculteur/Producteur
  └── Travailleur agricole
  └── Consommateur

MISSIONS (module main-d'oeuvre)
  ├── Publier offre — Étape 1 Description
  ├── Publier offre — Étape 2 Localisation + Rémunération
  ├── Publier offre — Étape 3 Récapitulatif
  ├── Liste candidatures + filtres
  ├── Profil travailleur (bottom sheet)
  ├── Contrat numérique + signature
  └── Suivi de mission en cours

MARCHÉ (module produits)
  ├── Publier produit
  ├── Catalogue (grille + liste)
  ├── Fiche détail produit
  ├── Panier
  ├── Checkout + livraison
  └── Confirmation commande

PAIEMENT
  ├── Sélecteur méthode (MTN MoMo / Orange / Wallet)
  ├── Confirmation paiement
  ├── Succès / Échec
  └── Portefeuille (solde + transactions + retrait)

MESSAGERIE
  ├── Liste conversations
  └── Chat (messages + cartes contrat/paiement inline)

PROFIL
  ├── Mon profil
  ├── Profil public
  └── Paramètres

NOTIFICATIONS
  └── Centre de notifications

ÉTATS SYSTÈME
  ├── Hors ligne
  ├── Chargement (skeleton)
  ├── Liste vide
  ├── Succès
  └── Erreur serveur
```

---

## ✨ Animations & Micro-interactions

```
Transition écrans     slide horizontal (push/pop) · fade pour modals
Pull-to-refresh       grain de blé animé qui tourne
Tap bouton            scale(0.97) + haptic léger
Succès paiement       confetti vert + check animé
Loading               skeleton shimmer (gauche → droite) · jamais spinner seul
Erreur input          shake + border rouge
Géoloc carte          pulse animé au chargement
Notification badge    animation pop (scale 0→1) sur l'icône
OTP validé            case verte + check
Onboarding slide      illustration entre par la droite
KYC check             progress step passe au vert avec ease-out
```

---

## 🖼️ Illustrations & Icônes

```
Icônes     Phosphor Icons
           regular → état inactif
           fill    → état actif / sélectionné
           taille standard : 24x24dp
           taille nav bar  : 26x26dp

Illustrations
           Style : flat illustration africain moderne
           Personnages : peau foncée, tenues camerounaises
           Décors : champs, marchés, villages ruraux
           Palette : cohérente avec design system

Photos produits
           Format bannière  : 16:9, radius 12px
           Format grille    : 1:1, radius 12px
           Toujours compressées pour 3G
```

---

## ♿ Accessibilité

```
Zone tactile minimum   44x44dp
Contraste texte        ≥ 4.5:1 (WCAG AA)
Taille texte minimum   13px
Focus visible          outline 2px #1B6CA8
Images                 alt text descriptif
Erreurs                texte + icône (jamais couleur seule)
```

---

## 📏 Formats & Tailles cibles

```
Largeur de référence   390px (iPhone 14 / Pixel 7)
Minimum supporté       360px (Android entrée de gamme)
OS minimum             Android 8+ (1 Go RAM) · iOS 14+
Mode offline           consultation catalogue + missions sans connexion
Mode économie données  images compressées + lazy loading
```

---

## 🔲 Grille de mise en page

```
Photos produit (grille)     2 colonnes · gap 12px
Cards horizontales           1 colonne · gap 12px
Stats (header)               3 colonnes égales
Actions rapides (home)       2x2 grid · gap 12px
Candidatures / missions      1 colonne liste complète
```

---

## 💬 Tonalité & Langue

```
Langue principale   Français
Secondaire          Anglais (logos partenaires uniquement)
Futur               Fulfulde, Ewondo (langues locales)

Ton       Chaleureux, accessible, direct
          Pas de jargon technique visible
          Chiffres toujours en FCFA
          Émojis utilisés avec modération comme accents : 🌱 🌾 💰 ⭐ 📍 ✅
```

---

*AgriConnect Design System · v1.0 · Avril 2026*
