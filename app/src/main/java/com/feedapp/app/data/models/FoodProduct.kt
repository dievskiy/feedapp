/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.feedapp.app.data.models.localdb.IProduct

/**
 * Class that is exported from FoodDatabase
 */
@Keep
@Entity(tableName = "food")
data class FoodProduct(
    @PrimaryKey @ColumnInfo(name = "id") override var id: Int,
    @ColumnInfo(name = "cryptoxanthin") val cryptoxanthin: Float? = 0f,
    @ColumnInfo(name = "totalfolates") val totalfolates: Float? = 0f,
    @ColumnInfo(name = "ergocalciferol_d2") val ergocalciferol_d2: Float? = 0f,
    @ColumnInfo(name = "niacin_b3") val niacin_b3: Float? = 0f,
    @ColumnInfo(name = "cobalamin_b12") val cobalamin_b12: Float? = 0f,
    @ColumnInfo(name = "energy_without_dietary_fibre") val energy_without_dietary_fibre: Float? = 0f,
    @ColumnInfo(name = "carbs") override val carbs: Float,
    @ColumnInfo(name = "fluoride") val fluoride: Float? = 0f,
    @ColumnInfo(name = "pantothenic_acid_b5") val pantothenic_acid_b5: Float? = 0f,
    @ColumnInfo(name = "thiamin_b1") val thiamin_b1: Float? = 0f,
    @ColumnInfo(name = "folicacid") val folicacid: Float? = 0f,
    @ColumnInfo(name = "retinol") val retinol: Float? = 0f,
    @ColumnInfo(name = "alpha_carotene") val alpha_carotene: Float? = 0f,
    @ColumnInfo(name = "pyridoxine_b6") val pyridoxine_b6: Float? = 0f,
    @ColumnInfo(name = "protein") override val proteins: Float,
    @ColumnInfo(name = "fat") override val fats: Float,
    @ColumnInfo(name = "tin") val tin: Float? = 0f,
    @ColumnInfo(name = "chloride") val chloride: Float? = 0f,
    @ColumnInfo(name = "omega_g") val omega_g: Float? = 0f,
    @ColumnInfo(name = "zinc") val zinc: Float? = 0f,
    @ColumnInfo(name = "o_poly_fats_g") val o_poly_fats_g: Float? = 0f,
    @ColumnInfo(name = "energy") val energy: Float? = 0f,
    @ColumnInfo(name = "molybdenum") val molybdenum: Float? = 0f,
    @ColumnInfo(name = "phosphorus") val phosphorus: Float? = 0f,
    @ColumnInfo(name = "provitamin_a") val provitamin_a: Float? = 0f,
    @ColumnInfo(name = "alcohol") val alcohol: Float? = 0f,
    @ColumnInfo(name = "total_dietary_fibre") val total_dietary_fibre: Float? = 0f,
    @ColumnInfo(name = "sat_fats_g") val sat_fats_g: Float? = 0f,
    @ColumnInfo(name = "vitamin_c") val vitamin_c: Float? = 0f,
    @ColumnInfo(name = "vitamin_e") val vitamin_e: Float? = 0f,
    @ColumnInfo(name = "magnesium") val magnesium: Float? = 0f,
    @ColumnInfo(name = "galactose") val galactose: Float? = 0f,
    @ColumnInfo(name = "moisture") val moisture: Float? = 0f,
    @ColumnInfo(name = "folatenatural") val folatenatural: Float? = 0f,
    @ColumnInfo(name = "sucrose") val sucrose: Float? = 0f,
    @ColumnInfo(name = "arsenic") val arsenic: Float? = 0f,
    @ColumnInfo(name = "omega") val omega: Float? = 0f,
    @ColumnInfo(name = "sodium") val sodium: Float? = 0f,
    @ColumnInfo(name = "beta_carotene") val beta_carotene: Float? = 0f,
    @ColumnInfo(name = "name") override var name: String,
    @ColumnInfo(name = "cadmium") val cadmium: Float? = 0f,
    @ColumnInfo(name = "vitamin_a_retinol_equivalents") val vitamin_a_retinol_equivalents: Float? = 0f,
    @ColumnInfo(name = "sugar") val sugar: Float? = 0f,
    @ColumnInfo(name = "o_poly_fats") val o_poly_fats: Float? = 0f,
    @ColumnInfo(name = "cholecalciferol_d3") val cholecalciferol_d3: Float? = 0f,
    @ColumnInfo(name = "potassium") val potassium: Float? = 0f,
    @ColumnInfo(name = "mercury") val mercury: Float? = 0f,
    @ColumnInfo(name = "dietary_folate_equivalents") val dietary_folate_equivalents: Float? = 0f,
    @ColumnInfo(name = "cobalt") val cobalt: Float? = 0f,
    @ColumnInfo(name = "lactose") val lactose: Float? = 0f,
    @ColumnInfo(name = "manganese") val manganese: Float? = 0f,
    @ColumnInfo(name = "biotin_b7") val biotin_b7: Float? = 0f,
    @ColumnInfo(name = "maltose") val maltose: Float? = 0f,
    @ColumnInfo(name = "maltotriose") val maltotriose: Float? = 0f,
    @ColumnInfo(name = "mono_fats") val mono_fats: Float? = 0f,
    @ColumnInfo(name = "selenium") val selenium: Float? = 0f,
    @ColumnInfo(name = "copper") val copper: Float? = 0f,
    @ColumnInfo(name = "iodine") val iodine: Float? = 0f,
    @ColumnInfo(name = "t_poly_fats_g") val t_poly_fats_g: Float? = 0f,
    @ColumnInfo(name = "nickel") val nickel: Float? = 0f,
    @ColumnInfo(name = "glucose") val glucose: Float? = 0f,
    @ColumnInfo(name = "chromium") val chromium: Float? = 0f,
    @ColumnInfo(name = "antimony") val antimony: Float? = 0f,
    @ColumnInfo(name = "calcium") val calcium: Float? = 0f,
    @ColumnInfo(name = "sulphur") val sulphur: Float? = 0f,
    @ColumnInfo(name = "nitrogen") val nitrogen: Float? = 0f,
    @ColumnInfo(name = "fructose") val fructose: Float? = 0f,
    @ColumnInfo(name = "lead") val lead: Float? = 0f,
    @ColumnInfo(name = "sat_fats") val sat_fats: Float? = 0f,
    @ColumnInfo(name = "mono_fats_g") val mono_fats_g: Float? = 0f,
    @ColumnInfo(name = "ash") val ash: Float? = 0f,
    @ColumnInfo(name = "aluminium") val aluminium: Float? = 0f,
    @ColumnInfo(name = "t_poly_fats") val t_poly_fats: Float? = 0f,
    @ColumnInfo(name = "iron") val iron: Float? = 0f,
    @ColumnInfo(name = "starch") val starch: Float? = 0f,
    @ColumnInfo(name = "riboflavin_b2") val riboflavin_b2: Float? = 0f,
    @ColumnInfo(name="calories") override val calories: Float
) : Comparable<FoodProduct>, IProduct{

    override fun compareTo(other: FoodProduct): Int {
        if (energy == other.energy && name == other.name && proteins == other.proteins && sat_fats == other.sat_fats_g
            && alcohol == other.alcohol
        ) return 0
        return 1
    }

    constructor() : this(
        id = 12,
        name = "",
        carbs = 0f,
        proteins = 0f,
        fats = 0f,
        calories = 0f
    )

    override fun toString(): String {
        return "name = $name id = $id protein = $proteins carbs = $carbs fats = $fats calories = $calories"
    }

}