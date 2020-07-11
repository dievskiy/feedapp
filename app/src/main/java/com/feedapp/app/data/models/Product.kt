/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.feedapp.app.util.ENERGY_TO_CALORIES_MULTIPLICATOR
import com.feedapp.app.util.round

/**
 * all fields are counted with respect to consumed grams.
 * class used for saved foodproduct with grams consumed
 */
@Entity(tableName = "products")
data class Product constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var foodProductId: Int = 0,
    var eatenGrams: Float = 0f,
    var consumedCalories: Float = 0f,
    var name: String = "",
    var consumedCryptoxanthin: Float? = null,
    var consumedTotalfolates: Float? = null,
    var consumedErgocalciferol_d2: Float? = null,
    var consumedNiacin_b3: Float? = null,
    var consumedCobalamin_b12: Float? = null,
    var consumedEnergy_without_dietary_fibre: Float? = null,
    var consumedCarbs: Float? = null,
    var consumedFluoride: Float? = null,
    var consumedPantothenic_acid_b5: Float? = null,
    var consumedThiamin_b1: Float? = null,
    var consumedFolicacid: Float? = null,
    var consumedRetinol: Float? = null,
    var consumedAlpha_carotene: Float? = null,
    var consumedPyridoxine_b6: Float? = null,
    var consumedProtein: Float? = null,
    var consumedFat: Float? = null,
    var consumedTin: Float? = null,
    var consumedChloride: Float? = null,
    var consumedOmega_g: Float? = null,
    var consumedZinc: Float? = null,
    var consumedO_poly_fats_g: Float? = null,
    var consumedEnergy: Float? = consumedCalories / ENERGY_TO_CALORIES_MULTIPLICATOR,
    var consumedMolybdenum: Float? = null,
    var consumedPhosphorus: Float? = null,
    var consumedProvitamin_a: Float? = null,
    var consumedAlcohol: Float? = null,
    var consumedTotal_dietary_fiber: Float? = null,
    var consumedSat_fats_g: Float? = null,
    var consumedVitamin_c: Float? = null,
    var consumedVitamin_e: Float? = null,
    var consumedMagnesium: Float? = null,
    var consumedGalactose: Float? = null,
    var consumedMoisture: Float? = null,
    var consumedFolatenatural: Float? = null,
    var consumedSucrose: Float? = null,
    var consumedArsenic: Float? = null,
    var consumedOmega: Float? = null,
    var consumedSodium: Float? = null,
    var consumedBeta_carotene: Float? = null,
    var consumedCadmium: Float? = null,
    var consumedVitamin_a_retinol_equivalents: Float? = null,
    var consumedSugar: Float? = null,
    var consumedO_poly_fats: Float? = null,
    var consumedCholecalciferol_d3: Float? = null,
    var consumedPotassium: Float? = null,
    var consumedMercury: Float? = null,
    var consumedDietary_folate_equivalents: Float? = null,
    var consumedCobalt: Float? = null,
    var consumedLactose: Float? = null,
    var consumedManganese: Float? = null,
    var consumedBiotin_b7: Float? = null,
    var consumedMaltose: Float? = null,
    var consumedMaltotriose: Float? = null,
    var consumedMono_fats: Float? = null,
    var consumedSelenium: Float? = null,
    var consumedCopper: Float? = null,
    var consumedIodine: Float? = null,
    var consumedT_poly_fats_g: Float? = null,
    var consumedNickel: Float? = null,
    var consumedGlucose: Float? = null,
    var consumedChromium: Float? = null,
    var consumedAntimony: Float? = null,
    var consumedCalcium: Float? = null,
    var consumedSulphur: Float? = null,
    var consumedNitrogen: Float? = null,
    var consumedFructose: Float? = null,
    var consumedLead: Float? = null,
    var consumedSat_fats: Float? = null,
    var consumedMono_fats_g: Float? = null,
    var consumedAsh: Float? = null,
    var consumedAluminium: Float? = null,
    var consumedT_poly_fats: Float? = null,
    var consumedIron: Float? = null,
    var consumedStarch: Float? = null,
    var consumedRiboflavin_b2: Float? = null,
    var consumedCholesterol: Float? = null,
    var consumedVitaminD: Float? = consumedCholecalciferol_d3?.plus(
        consumedErgocalciferol_d2 ?: 0f
    ),
    var consumedVitaminA: Float? = consumedRetinol?.plus(
        consumedVitamin_a_retinol_equivalents ?: 0f
    )?.plus(consumedProvitamin_a ?: 0f)
) : Comparable<Product> {

    override fun compareTo(other: Product): Int {
        if (consumedCalories.round(3) == other.consumedCalories.round(3)
            && foodProductId == other.foodProductId
            && name == other.name
            && consumedProtein?.round(3) == other.consumedProtein?.round(3)
            && consumedCarbs?.toInt() == other.consumedCarbs?.toInt()
            && consumedFat?.toInt() == other.consumedFat?.toInt()
            && eatenGrams.round(3) == other.eatenGrams.round(3)
        ) return 0
        return 1
    }

    override fun toString(): String {
        return "Product: id = $id "
    }


}