package com.feedapp.app.data.databases.daos

import androidx.room.*
import com.feedapp.app.data.models.Product


@Dao
interface ProductDao {

    @Query("select * from products")
    fun getAllProducts():List<Product>

    @Query("select * from products where id == :id")
    fun searchById(id: Int):List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @Query("select rowid from products order by ROWID desc limit 1")
    fun getSize():Int

    @Delete
    fun deleteProduct(product: Product)

    @Query("delete from products")
    fun deleteAllProducts()


}