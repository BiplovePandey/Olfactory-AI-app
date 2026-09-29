package com.example.olfactory.ml

import com.example.data.model.CompatibilityBreakdown
import com.example.data.model.Fragrance
import com.example.data.model.LayeringResult
import com.example.data.model.OlfactoryVector8D
import com.example.data.model.UserPreferences
import com.example.data.model.WhyItWorks
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

object OlfactoryEngine {

    private data class ChordSynergy(val score: Int, val chordName: String, val reason: String)

    // Pairwise chord synergy matrix (harmony of complementary olfactory families)
    private val CHORD_COMPATIBILITY: Map<String, Map<String, ChordSynergy>> = mapOf(
        "citrus" to mapOf(
            "woody" to ChordSynergy(95, "Solar Citrus & Sacred Woods", "Sparkling citrus elevates the rich woody base, creating crisp contrast and refined longevity"),
            "gourmand" to ChordSynergy(85, "Zesty Praline Fusion", "Zesty citrus cuts through heavy sweetness, preventing cloying notes while adding effervescence"),
            "amber" to ChordSynergy(88, "Golden Amber Radiance", "Bright citrus illuminates deep resinous amber with golden warmth"),
            "floral" to ChordSynergy(90, "Blossom & Bergamot Veil", "Fresh bergamot and neroli brighten lush floral petals for effortless daytime elegance"),
            "spicy" to ChordSynergy(82, "Aromatic Pepper Zing", "Crisp citrus balances exotic pepper and cardamom with a lively zing"),
            "fresh" to ChordSynergy(75, "Double Aqua Cascade", "Double fresh notes are pleasant and clean, though lower in contrast and depth"),
            "earthy" to ChordSynergy(97, "Petrichor & Sunlit Bergamot", "Sunlit Italian citrus illuminates damp baked monsoon earth, recreating fresh petrichor after a thunderstorm")
        ),
        "woody" to mapOf(
            "gourmand" to ChordSynergy(96, "Smoky Vanilla Suede", "Creamy sandalwood and cedar provide a sturdy foundation that anchors sweet vanilla and tonka"),
            "floral" to ChordSynergy(92, "Rosewood Velvet Sillage", "Smoky woods ground romantic rose and jasmine, lending unisex allure and mysterious character"),
            "spicy" to ChordSynergy(90, "Cardamom & Cedar Alchemy", "Warm spices like cardamom and cinnamon weave seamlessly into deep cedarwood and vetiver"),
            "amber" to ChordSynergy(92, "Regal Resinous Flacon", "Rich resins blend into sacred woods for an opulent, regal sillage"),
            "citrus" to ChordSynergy(95, "Solar Citrus & Sacred Woods", "Grounding woods anchor luminous citrus top notes through the drydown"),
            "earthy" to ChordSynergy(93, "Sacred Clay & Sandalwood", "Ancient riverbed clay and sacred sandalwood meld into a meditative, grounding aura")
        ),
        "floral" to mapOf(
            "gourmand" to ChordSynergy(88, "Candied Petal Nectar", "Lush floral bouquets meet warm praline or vanilla, developing a delectable sensual trail"),
            "woody" to ChordSynergy(92, "Rosewood Velvet Sillage", "Velvety rose and iris gain modern sophistication against dry cedar and smoky birch"),
            "spicy" to ChordSynergy(86, "Peppercorn Blossom", "Pink pepper and nutmeg add provocative tension to delicate white florals"),
            "citrus" to ChordSynergy(90, "Blossom & Bergamot Veil", "Effervescent citrus blossoms into rich floral bouquets"),
            "earthy" to ChordSynergy(94, "Monsoon Jasmine & Wet Soil", "Indian Sambac Jasmine or Damask Rose blooms with intoxicating realism against damp alluvial petrichor")
        ),
        "gourmand" to mapOf(
            "spicy" to ChordSynergy(92, "Spiced Vanilla Chai", "Fiery spices temper creamy caramel and Madagascar vanilla with sultry warmth"),
            "woody" to ChordSynergy(96, "Smoky Vanilla Suede", "Earthy vetiver or sandalwood balances sweet confectionery notes with refined restraint"),
            "citrus" to ChordSynergy(85, "Zesty Praline Fusion", "Acidic citrus cuts through dense honey or vanilla gourmands"),
            "gourmand" to ChordSynergy(45, "Double Sugar Density", "Stacking two heavy sweet gourmands risks becoming overpowering and overly dense"),
            "earthy" to ChordSynergy(87, "Dark Earth & Ambered Cocoa", "Earthy roots cut through sweet vanilla, providing sophisticated gourmand depth")
        ),
        "spicy" to mapOf(
            "woody" to ChordSynergy(90, "Cardamom & Cedar Alchemy", "Exotic spices infuse dry woods with hypnotic warmth"),
            "floral" to ChordSynergy(86, "Peppercorn Blossom", "Cardamom and clove impart seductive edge to delicate florals"),
            "citrus" to ChordSynergy(82, "Aromatic Pepper Zing", "Sharp citrus zest refreshes warm aromatic spices"),
            "gourmand" to ChordSynergy(92, "Spiced Vanilla Chai", "Warm baking spices bring edible depth to rich vanillic bases"),
            "earthy" to ChordSynergy(91, "Himalayan Clove & Wet Roots", "Warm Himalayan spices blend effortlessly with damp earth and vetiver roots")
        ),
        "earthy" to mapOf(
            "citrus" to ChordSynergy(97, "Petrichor & Sunlit Bergamot", "Sparkling citrus lifts petrichor and clay into an effervescent summer storm signature"),
            "floral" to ChordSynergy(94, "Monsoon Jasmine & Wet Soil", "Damp monsoon soil anchors lush rose and jasmine petals with organic intimacy"),
            "woody" to ChordSynergy(93, "Sacred Clay & Sandalwood", "Sacred sandalwood and vetiver amplify baked clay into a serene meditative aura"),
            "spicy" to ChordSynergy(91, "Himalayan Clove & Wet Roots", "Zesty pepper and cardamom animate damp alluvial soil with radiant warmth"),
            "gourmand" to ChordSynergy(87, "Dark Earth & Ambered Cocoa", "Clay and vetiver roots ground sweet tonka and vanilla with earthy sophistication")
        )
    )

    /**
     * Extracts normalized 8-dimensional feature vector according to the Olfactory Vector Space:
     * [Freshness, Sweetness, Intensity, Woody, Floral, Warm Resinous / Spices, Earthy / Clay, Longevity / Fixative]
     */
    fun extractVector(fragrance: Fragrance): FloatArray {
        val sweetness = max(0.1f, min(1.0f, fragrance.sweetness / 10f))
        val freshness = max(0.1f, min(1.0f, fragrance.freshness / 10f))
        val intensity = max(0.1f, min(1.0f, fragrance.intensity / 10f))

        var woodyHits = 0f
        var floralHits = 0f
        var warmResinousSpicesHits = 0f
        var earthyClayHits = 0f
        var longevityFixativeHits = 0f

        val allNotes = (fragrance.top_notes + fragrance.middle_notes + fragrance.base_notes).map { it.lowercase() }
        val familyLower = fragrance.fragrance_family.lowercase()

        if (familyLower.contains("woody") || familyLower.contains("cedar") || familyLower.contains("sandalwood") || familyLower.contains("oud")) woodyHits += 2f
        if (familyLower.contains("floral") || familyLower.contains("rose") || familyLower.contains("jasmine")) floralHits += 2f
        if (familyLower.contains("spicy") || familyLower.contains("oriental") || familyLower.contains("amber") || familyLower.contains("resin")) warmResinousSpicesHits += 2f
        if (familyLower.contains("earth") || familyLower.contains("clay") || familyLower.contains("petrichor") || familyLower.contains("mitti") || familyLower.contains("chypre")) earthyClayHits += 2f

        val longHours = fragrance.longevity.filter { it.isDigit() }.toIntOrNull() ?: 6
        longevityFixativeHits += min(3f, max(1f, longHours / 3f))

        if (fragrance.is_oil_based || fragrance.format == "Attar" || fragrance.format == "Extrait de Parfum") {
            longevityFixativeHits += 2f
        }

        allNotes.forEach { note ->
            if (note.contains("mitti") || note.contains("petrichor") || note.contains("earth") || note.contains("clay") || note.contains("soil") || note.contains("moss") || note.contains("oakmoss") || note.contains("vetiver") || note.contains("khus")) {
                earthyClayHits += 1.5f
            }
            if (note.contains("wood") || note.contains("cedar") || note.contains("sandalwood") || note.contains("chandan") || note.contains("oud") || note.contains("agarwood") || note.contains("patchouli") || note.contains("cypress") || note.contains("guaiac") || note.contains("pine")) {
                woodyHits += 1.5f
            }
            if (note.contains("rose") || note.contains("gulab") || note.contains("jasmine") || note.contains("mogra") || note.contains("tuberose") || note.contains("rajnigandha") || note.contains("kewra") || note.contains("kewda") || note.contains("champa") || note.contains("neroli") || note.contains("iris") || note.contains("nargis") || note.contains("lily") || note.contains("violet") || note.contains("lavender")) {
                floralHits += 1.5f
            }
            if (note.contains("amber") || note.contains("spice") || note.contains("cardamom") || note.contains("elaichi") || note.contains("cinnamon") || note.contains("clove") || note.contains("saffron") || note.contains("kesar") || note.contains("zafran") || note.contains("shamama") || note.contains("pepper") || note.contains("resin") || note.contains("myrrh") || note.contains("frankincense") || note.contains("benzoin") || note.contains("loban") || note.contains("vanilla") || note.contains("tonka") || note.contains("incense")) {
                warmResinousSpicesHits += 1.5f
            }
            if (note.contains("musk") || note.contains("ambergris") || note.contains("ambroxan") || note.contains("civet") || note.contains("sandalwood") || note.contains("agarwood") || note.contains("oud")) {
                longevityFixativeHits += 1.2f
            }
        }

        val totalHits = max(1f, woodyHits + floralHits + warmResinousSpicesHits + earthyClayHits)
        val woody = min(1.0f, woodyHits / totalHits * 1.5f)
        val floral = min(1.0f, floralHits / totalHits * 1.5f)
        val warmResinousSpices = min(1.0f, warmResinousSpicesHits / totalHits * 1.5f)
        val earthyClay = min(1.0f, earthyClayHits / totalHits * 1.5f)
        val longevityFixative = min(1.0f, max(0.2f, longevityFixativeHits / 5f))

        return floatArrayOf(
            freshness,
            sweetness,
            intensity,
            woody,
            floral,
            warmResinousSpices,
            earthyClay,
            longevityFixative
        )
    }

    fun getVector8D100(fragrance: Fragrance): OlfactoryVector8D {
        val v = extractVector(fragrance)
        return OlfactoryVector8D(
            freshness = (v[0] * 100).roundToInt(),
            sweetness = (v[1] * 100).roundToInt(),
            intensity = (v[2] * 100).roundToInt(),
            woody = (v[3] * 100).roundToInt(),
            floral = (v[4] * 100).roundToInt(),
            warm_resinous_spices = (v[5] * 100).roundToInt(),
            earthy_clay = (v[6] * 100).roundToInt(),
            longevity_fixative = (v[7] * 100).roundToInt()
        )
    }

    /**
     * Computes Cosine Similarity between two N-dimensional vectors
     */
    fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        var dot = 0f
        var normA = 0f
        var normB = 0f
        val len = min(v1.size, v2.size)
        for (i in 0 until len) {
            dot += v1[i] * v2[i]
            normA += v1[i] * v1[i]
            normB += v2[i] * v2[i]
        }
        val denom = sqrt(normA) * sqrt(normB)
        return if (denom == 0f) 0f else dot / denom
    }

    fun createUserPreferenceVector(preferences: UserPreferences): FloatArray {
        val sweetness = preferences.sweetness / 10f
        val freshness = preferences.freshness / 10f
        val intensity = preferences.intensity / 10f
        var woody = 0.3f
        var floral = 0.3f
        var warmResinousSpices = 0.3f
        var earthyClay = 0.2f
        val longevityFixative = 0.6f

        val families = preferences.favorite_family.map { it.lowercase() }
        if (families.any { it.contains("wood") || it.contains("oud") || it.contains("cedar") }) woody = 0.9f
        if (families.any { it.contains("floral") || it.contains("rose") || it.contains("jasmine") }) floral = 0.9f
        if (families.any { it.contains("spic") || it.contains("amber") || it.contains("oriental") || it.contains("gourmand") }) warmResinousSpices = 0.9f
        if (families.any { it.contains("earth") || it.contains("petrichor") || it.contains("mitti") || it.contains("chypre") }) earthyClay = 0.9f

        preferences.preferred_notes.forEach { n ->
            val lower = n.lowercase()
            if (lower.contains("wood") || lower.contains("sandal") || lower.contains("cedar") || lower.contains("oud")) woody = min(1.0f, woody + 0.3f)
            if (lower.contains("rose") || lower.contains("jasmine") || lower.contains("floral") || lower.contains("mogra")) floral = min(1.0f, floral + 0.3f)
            if (lower.contains("amber") || lower.contains("spice") || lower.contains("saffron") || lower.contains("cardamom")) warmResinousSpices = min(1.0f, warmResinousSpices + 0.3f)
            if (lower.contains("mitti") || lower.contains("khus") || lower.contains("earth") || lower.contains("clay") || lower.contains("vetiver")) earthyClay = min(1.0f, earthyClay + 0.4f)
        }

        return floatArrayOf(
            freshness,
            sweetness,
            intensity,
            woody,
            floral,
            warmResinousSpices,
            earthyClay,
            longevityFixative
        )
    }

    private fun getDominantCategory(family: String, notes: List<String>): String {
        val combined = (family + " " + notes.joinToString(" ")).lowercase()
        return when {
            combined.contains("mitti") || combined.contains("petrichor") || combined.contains("earth") || combined.contains("clay") -> "earthy"
            combined.contains("wood") || combined.contains("cedar") || combined.contains("sandalwood") || combined.contains("chandan") || combined.contains("vetiver") || combined.contains("khus") || combined.contains("oud") -> "woody"
            combined.contains("citrus") || combined.contains("bergamot") || combined.contains("lemon") || combined.contains("lime") || combined.contains("kewra") -> "citrus"
            combined.contains("vanilla") || combined.contains("gourmand") || combined.contains("tonka") || combined.contains("praline") || combined.contains("chai") -> "gourmand"
            combined.contains("floral") || combined.contains("rose") || combined.contains("gulab") || combined.contains("jasmine") || combined.contains("mogra") || combined.contains("motia") || combined.contains("nargis") -> "floral"
            combined.contains("spic") || combined.contains("cardamom") || combined.contains("cinnamon") || combined.contains("pepper") || combined.contains("kesar") || combined.contains("saffron") || combined.contains("shamama") -> "spicy"
            combined.contains("amber") || combined.contains("resin") -> "amber"
            else -> "fresh"
        }
    }

    private fun calculateNoteCompatibility(fragA: Fragrance, fragB: Fragrance): Pair<Int, ChordSynergy> {
        val catA = getDominantCategory(fragA.fragrance_family, fragA.top_notes + fragA.base_notes)
        val catB = getDominantCategory(fragB.fragrance_family, fragB.top_notes + fragB.base_notes)

        val lookup = CHORD_COMPATIBILITY[catA]?.get(catB) ?: CHORD_COMPATIBILITY[catB]?.get(catA)
        if (lookup != null) {
            return Pair(lookup.score, lookup)
        }

        if (catA == catB) {
            return when (catA) {
                "gourmand" -> Pair(48, ChordSynergy(48, "Dense Gourmand Echo", "Both scents are heavily sweet; pairing may feel overpowering."))
                "woody" -> Pair(82, ChordSynergy(82, "Resonant Wood Architecture", "Harmonious woody resonance with rich texture, creating an enduring signature."))
                "earthy" -> Pair(85, ChordSynergy(85, "Alluvial Monsoon Resonance", "Deep petrichor and root resonance evocative of cooling monsoons."))
                else -> Pair(78, ChordSynergy(78, "Monochromatic Accord", "Complementary nuances within the $catA spectrum create a cohesive scent bubble."))
            }
        }

        return Pair(82, ChordSynergy(82, "Harmonic Contrast", "Balanced contrast between distinct olfactory profiles."))
    }

    private fun calculateDiversityFactor(cosSim: Float): Int {
        return when {
            cosSim >= 0.95f -> 30
            cosSim >= 0.85f -> 60
            cosSim in 0.40f..0.80f -> 98 // Sweet spot of complementary contrast!
            cosSim in 0.20f..0.40f -> 85
            else -> 65
        }
    }

    /**
     * Evaluates a pair of fragrances using the Olfactory AI harmony engine
     */
    fun scoreLayeringPair(
        fragA: Fragrance,
        fragB: Fragrance,
        preferences: UserPreferences = UserPreferences()
    ): LayeringResult? {
        if (fragA.id == fragB.id) return null

        val vectorA = extractVector(fragA)
        val vectorB = extractVector(fragB)

        // 1. Note compatibility (40%)
        val (noteCompScore, chordSynergy) = calculateNoteCompatibility(fragA, fragB)

        // 2. User preference match (20%)
        val prefVector = createUserPreferenceVector(preferences)
        val userSimA = cosineSimilarity(prefVector, vectorA)
        val userSimB = cosineSimilarity(prefVector, vectorB)
        val prefMatchScore = (((userSimA + userSimB) / 2f) * 100f).roundToInt()

        // 3. Season compatibility (15%)
        val targetSeason = preferences.season
        val aHasSeason = fragA.season.any { it.equals(targetSeason, ignoreCase = true) }
        val bHasSeason = fragB.season.any { it.equals(targetSeason, ignoreCase = true) }
        val seasonScore = if (aHasSeason && bHasSeason) 100 else if (aHasSeason || bHasSeason) 75 else 45

        // 4. Occasion compatibility (15%)
        val targetOccasion = preferences.occasion
        val aHasOccasion = fragA.occasion.any { it.equals(targetOccasion, ignoreCase = true) }
        val bHasOccasion = fragB.occasion.any { it.equals(targetOccasion, ignoreCase = true) }
        val occasionScore = if (aHasOccasion && bHasOccasion) 100 else if (aHasOccasion || bHasOccasion) 75 else 45

        // 5. Complementary note & Diversity score (10%)
        val pairwiseSim = cosineSimilarity(vectorA, vectorB)
        var diversityFactor = calculateDiversityFactor(pairwiseSim)

        // Cross-origin detection and origin style categorization
        val isAIndian = fragA.origin_style.contains("indian", ignoreCase = true) ||
                (fragA.brand_country ?: "").contains("india", ignoreCase = true) || fragA.is_oil_based
        val isBIndian = fragB.origin_style.contains("indian", ignoreCase = true) ||
                (fragB.brand_country ?: "").contains("india", ignoreCase = true) || fragB.is_oil_based
        val isCrossOrigin = (isAIndian && !isBIndian) || (!isAIndian && isBIndian)

        val originPairingType = when {
            isCrossOrigin -> {
                diversityFactor = min(100, diversityFactor + 10)
                "cross_origin_fusion"
            }
            isAIndian && isBIndian -> "pure_indian_heritage"
            else -> "pure_western_luxury"
        }

        // Layering Score Formula:
        // 40% compatibility + 20% user preference + 15% season + 15% occasion + 10% diversity
        val totalScore = (
            (0.40f * noteCompScore) +
            (0.20f * prefMatchScore) +
            (0.15f * seasonScore) +
            (0.15f * occasionScore) +
            (0.10f * diversityFactor)
        ).roundToInt()

        val clampedScore = min(99, max(30, totalScore))

        // Application tips and layering ritual
        val isAOil = fragA.is_oil_based || fragA.format == "Attar" || fragA.format == "Concentrated Perfume Oil" || fragA.format == "Pure Oud Oil"
        val isBOil = fragB.is_oil_based || fragB.format == "Attar" || fragB.format == "Concentrated Perfume Oil" || fragB.format == "Pure Oud Oil"

        val baseScent: Fragrance
        val topScent: Fragrance
        val layeringMethod: String
        val applicationTip: String

        when {
            isAOil && !isBOil -> {
                baseScent = fragA
                topScent = fragB
                layeringMethod = "Attar Pulse-Point Base + Spray Diffusion (East-Meets-West Ritual)"
                applicationTip = "Dab 1-2 drops of ${baseScent.name} onto warm pulse points (wrists and throat). Wait 60-90 seconds for body heat to unlock the oil-based sandalwood and earthy resins, then spray 1-2 mists of ${topScent.name} across your collarbones. The alcohol mist projects radiant top notes while the botanical attar anchors all-day longevity."
            }
            isBOil && !isAOil -> {
                baseScent = fragB
                topScent = fragA
                layeringMethod = "Attar Pulse-Point Base + Spray Diffusion (East-Meets-West Ritual)"
                applicationTip = "Dab 1-2 drops of ${baseScent.name} onto warm pulse points (wrists and neck). Allow skin heat 60-90 seconds to warm the natural attar foundation, then mist ${topScent.name} from 6 inches away. This creates an ethereal diffusion bubble anchored by sacred natural botanicals."
            }
            isAOil && isBOil -> {
                val isAHeavier = fragA.intensity >= fragB.intensity
                baseScent = if (isAHeavier) fragA else fragB
                topScent = if (isAHeavier) fragB else fragA
                layeringMethod = "Dual Attar Pulse-Point Compounding"
                applicationTip = "Warm one drop of ${baseScent.name} between inner wrists as your grounding root. Gently dab a half-drop of ${topScent.name} on the hollow of your neck without rubbing vigorously. The two pure oils will fuse intimately with your body's natural chemistry."
            }
            else -> {
                val isAHeavier = (fragA.intensity + fragA.sweetness) >= (fragB.intensity + fragB.sweetness)
                baseScent = if (isAHeavier) fragA else fragB
                topScent = if (isAHeavier) fragB else fragA
                layeringMethod = "Dual Spray Sillage Synergy"
                applicationTip = "Apply 2 sprays of ${baseScent.name} first to warm pulse points (chest and throat) as your anchor. Wait 30 seconds, then mist 1-2 sprays of ${topScent.name} over collarbones and wrists for a shimmering, multidimensional diffusion."
            }
        }

        val openingNotes = topScent.top_notes.take(2).joinToString(" and ").ifEmpty { "bright accords" }
        val baseNotes = baseScent.base_notes.take(2).joinToString(" and ").ifEmpty { "deep resins and woods" }

        val openingHarmony = "The radiant opening of ${topScent.name} ($openingNotes) diffuses luminous vibrancy on initial contact, while ${baseScent.name} establishes an intimate, structured anchor."
        val drydownDepth = "As the accords settle, ${chordSynergy.reason.lowercase()}. The drydown marries $baseNotes with airy undertones, ensuring enduring sillage without olfactory fatigue."
        val fusionText = if (isCrossOrigin) " This cross-origin pairing harmoniously bridges Indian heritage botanicals with international luxury perfumery." else ""
        val explanation = "This bespoke layering creates a multifaceted olfactory signature. ${chordSynergy.reason}.$fusionText Tailor-made for ${preferences.season} atmospheres and ${preferences.occasion} occasions."

        val bestSeason = fragA.season.intersect(fragB.season.toSet()).firstOrNull() ?: fragA.season.firstOrNull() ?: "All Year"
        val bestOccasion = fragA.occasion.intersect(fragB.occasion.toSet()).firstOrNull() ?: fragA.occasion.firstOrNull() ?: "Signature"
        val bestTime = if (fragA.intensity + fragB.intensity >= 15) "Evening / Night" else "Daytime / All Day"

        return LayeringResult(
            id = "${fragA.id}-${fragB.id}",
            fragrance_a = fragA,
            fragrance_b = fragB,
            compatibility_score = clampedScore,
            chord_title = chordSynergy.chordName,
            breakdown = CompatibilityBreakdown(
                note_compatibility = noteCompScore,
                user_preference_match = prefMatchScore,
                season_compatibility = seasonScore,
                occasion_compatibility = occasionScore,
                complementary_note_score = diversityFactor,
                diversity_factor = diversityFactor
            ),
            is_cross_origin = isCrossOrigin,
            origin_pairing_type = originPairingType,
            layering_method = layeringMethod,
            explanation = explanation,
            why_it_works = WhyItWorks(
                opening_harmony = openingHarmony,
                drydown_depth = drydownDepth,
                application_tip = applicationTip
            ),
            best_season = bestSeason,
            best_occasion = bestOccasion,
            best_time_of_day = bestTime
        )
    }

    /**
     * Recommends top layering partners for a given fragrance from a pool
     */
    fun findBestPartners(
        target: Fragrance,
        pool: List<Fragrance>,
        preferences: UserPreferences = UserPreferences(),
        limit: Int = 4
    ): List<LayeringResult> {
        return pool
            .filter { it.id != target.id }
            .mapNotNull { scoreLayeringPair(target, it, preferences) }
            .sortedByDescending { it.compatibility_score }
            .take(limit)
    }
}
