/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.databases.entities

//
//@RunWith(RobolectricTestRunner::class)
//@Config(sdk = [Build.VERSION_CODES.O_MR1])
//class ProductDaoTest {
//
//    private lateinit var dao: ProductDao
//
//    @org.junit.Test
//    fun productDaoTestAll() {
//        CoroutineScope(IO).launch {
//            dao = UserDatabase.invoke(
//                InstrumentationRegistry.getInstrumentation().targetContext
//            ).getProductDao()
//            // create Product
//            val id = 123
//            val eatenGrams = 20f
//            val realId = 120
//            val product = Product(
//                id = realId,
//                foodProductId = id,
//                eatenGrams = eatenGrams
//            )
//            assertEquals(product.foodProductId, id)
//            assertEquals(product.eatenGrams, eatenGrams)
//            dao = UserDatabase.invoke(InstrumentationRegistry.getInstrumentation().targetContext)
//                .getProductDao()
//
//            // insert
//            dao.insertProduct(product)
//            assertEquals(product, dao.searchById(realId)[0])
//
//            // delete
//            dao.deleteProduct(product)
//            assertEquals(dao.getSize(), 0)
//        }
//    }
//
//}