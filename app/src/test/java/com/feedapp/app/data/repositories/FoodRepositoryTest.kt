/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.repositories

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.feedapp.app.data.databases.dbclasses.FoodDatabase
import com.feedapp.app.data.models.FoodProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(RobolectricTestRunner::class)
internal class FoodRepositoryTest {

    @Test
    fun testAll() {
        CoroutineScope(IO).launch {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val foodProductDao = FoodDatabase.invoke(context).getProductDao()
            val repository =
                FoodRepository(context = context, foodProductDao = foodProductDao)

            testInsertAndDelete(repository)
            searchByName(repository)
            getSize(repository)
            searchById(repository)
        }
    }

    private fun testInsertAndDelete(repository: FoodRepository) {

        val id = 201
        val proteinAmount = 12.11f
        val foodProduct = FoodProduct(
            id = id,
            name = "Cake",
            proteins = proteinAmount,
            calories = 0f,
            fats = 0f,
            carbs = 0f
        )

        repository.insertProduct(product = foodProduct)
        val received = repository.searchById(id)

        // test insertion and query
        assert(received is FoodProduct)
        assert(received == foodProduct)
        assert(received?.proteins!!.equals(proteinAmount))
        // test deletion
        repository.deleteProduct(product = foodProduct)

        val receivedAfterDeletion = repository.searchById(id)
        assert(receivedAfterDeletion == null)
    }

    private fun searchByName(repository: FoodRepository) {
        assert(repository.searchByName("Chicken").size == 13)
    }

    private fun getSize(repository: FoodRepository) {
        // there are 976 entries in db
        val size = 976
        val receivedSize = repository.getSize()
        assert(receivedSize == size)
    }

    private fun searchById(repository: FoodRepository) {
        assert(repository.searchById(141) is FoodProduct)
    }


}