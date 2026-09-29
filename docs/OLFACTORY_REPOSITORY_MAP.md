# Olfactory AI: Complete Repository & Technology Platform Manifest

## 1. Repository Structure & Ingestion Summary
The imported repository (`BiplovePandey/Fragrance-Layering-AI`, branch `android-app`) consists of **237 files** spanning:
- **Backend Service**: Express + Bun/Node server (`server.ts`, `server/db.ts`, `server/olfactoryMemory.ts`).
- **ML / Olfactory Intelligence**: Mathematical vector space, layering, clustering, similarity, wear engines, and canonical validation (`server/ml/`).
- **Relational Olfactory Database**: `fragrances.db` (SQLite) containing 12 production tables, 124 verified fragrance profiles, 80 brands, and 1,024 structured note relationships.
- **Frontend Web Application**: React 18, Vite, Tailwind CSS, Framer Motion, and rich visual perfumery modals (`src/`).
- **Curated Datasets**: Production JSON catalogs and Indian heritage manifests (`data/`).
- **Native Android Client**: Jetpack Compose application under `/app` consuming the shared technology platform.

---

## 2. Backend Architecture (`server.ts`, `server/db.ts`)
- **Server Framework**: Express with JSON body parser and CORS support.
- **Database Access**: `better-sqlite3` interfacing with `fragrances.db`.
- **Memory & Personalization Engine**: `server/olfactoryMemory.ts` tracking user behavior events, affinity scores, and decay functions.
- **Unified Context Engine**: Context normalization (`/api/context/normalize`) for seasonal/weather recommendations.

---

## 3. Frontend Architecture (`src/`)
- **Client App**: React 18 + Vite SPA (`src/App.tsx`).
- **Atelier Visual Systems**: Evaporation timeline (`EvaporationTimeline.tsx`), Atmospheric scent canvas, floating note bubbles, and discovery chambers.
- **Web API Layer**: `src/services/api.ts` connecting to Express `/api/*`.

---

## 4. Android Client Architecture (`/app`)
- **UI Framework**: Modern Jetpack Compose, Material 3, and Kotlin Coroutines/Flow.
- **Visual Design**: Obsidian Black (`#0D0D0E`) and Champagne Gold (`#D4AF37`, `#F2D06B`) Atelier aesthetics.
- **Local Persistence & Cache**: Room Database (`OlfactoryDatabase.kt`, `FragranceDao.kt`) pre-populated from production datasets.
- **Networking Contract**: Retrofit (`OlfactoryApiService.kt`) directly implementing the backend REST contract.
- **Edge Intelligence**: Native Kotlin port of the 8D Vector Engine (`OlfactoryEngine.kt`) providing offline harmony calculation matching `server/ml/layering.ts`.

---

## 5. Database Architecture (`fragrances.db`)
Twelve production tables:
1. `fragrances` (124 rows): Core perfumes and attars, notes, formats, and pre-computed 8D vectors.
2. `brands` (80 rows): Houses, origin styles (Indian Heritage, Attar, Niche, Designer), and countries.
3. `fragrance_notes` (1,024 rows): Normalized note associations (top, middle, base).
4. `note_taxonomy` (60 rows): Note categorization, families, and cultural context.
5. `user_collection` (5 active rows): User's owned flacons.
6. `user_preferences`: Scent intensity, sweetness, freshness affinities.
7. `layering_combinations`: Saved scent chords and synergy breakdowns.
8. `user_ratings`: Feedback loops for ML tuning.
9. `fragrance_journal`: Wear logs, longevity, projection, and weather conditions.
10. `olfactory_behavior_events`: Behavioral events feeding the memory engine.
11. `product_submissions`: Community flacon submissions.
12. `users`: User identity and session mapping.

---

## 6. Dataset Inventory (`/data` & `app/src/main/assets`)
- `fragrances.json` (124 entries): Production fragrance records.
- `brands.json` (80 entries): Brand details and heritage histories.
- `india_brand_manifest.json`: Verified Indian heritage houses (Kannauj, Assam, Mysore).
- `india_fragrance_manifest.json`: Traditional attars, tolas, and contemporary Indian blends.
- `notes_taxonomy.json`: Normalized scent taxonomy with English and cultural equivalents.
- `india_import_summary.json` & `step5d_audit_data.json`: Audit manifests.

---

## 7. API Route Inventory (`server.ts`)
- **Health**: `GET /api/health`
- **Catalog & Discovery**: `GET /api/fragrances`, `GET /api/fragrances/:id`, `GET /api/brands`, `GET /api/taxonomy`, `GET /api/clusters`
- **Layering & Recommendations**: 
  - `POST /api/layer` & `POST /api/layering/combinations`: 8D harmony scoring.
  - `POST /api/recommend`: Attribute-based partner recommendation.
  - `POST /api/recommend/wear`: Context-aware occasion recommendation.
- **Wardrobe & Saved**: `GET|POST|DELETE /api/collection`, `GET|POST|DELETE /api/saved-combinations`
- **Preferences & Journal**: `GET|POST /api/preferences`, `GET|POST /api/journal`
- **Olfactory Memory**: `POST /api/behavior/events`, `GET /api/behavior/memory`, `GET /api/behavior/history`

---

## 8. AI / ML & 8-Dimensional Olfactory Vector Engine (`server/ml/`)
- `features.ts`: Computes 8-dimensional normalized vectors:
  1. `Freshness`
  2. `Sweetness`
  3. `Intensity`
  4. `Woody`
  5. `Floral`
  6. `Warm Resinous / Spices`
  7. `Earthy / Clay`
  8. `Longevity / Fixative`
- `layering.ts`: Algorithmic harmony evaluation combining:
  - Vector cosine distance & complementary delta.
  - Shared & complementary note bonuses.
  - Cross-origin pairing bonus (East-meets-West / Attar + Spray).
  - Volatility hierarchy (Oil base layer + Alcohol diffusion topper).
- `wearEngine.ts`: Real-time weather, temperature, humidity, and occasion scoring.
- `similarity.ts`: Cosine and Euclidean scent similarity metrics.
- `clustering.ts`: K-means fragrance accord clustering.

---

## 9. Android Integration Strategy
- **Client Role**: Native Android client providing high-performance on-the-go exploration, hardware-accelerated Canvas radars, and offline capabilities.
- **Authoritative Backend**: Connects to the Express backend for server-side memory updates, journal logging, and community feeds.
- **Zero-Mock Policy**: All calculations match the exact production formulas in `server/ml/` and real datasets in `fragrances.db`.

---

## 10. Build Status & Next Steps
- Android app build: **SUCCEEDED (Verified)**.
- Unit & Robolectric test suite: **SUCCEEDED (100% Pass)**.
- Ingestion & Audit status: **COMPLETED**.
