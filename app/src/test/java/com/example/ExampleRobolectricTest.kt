package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.catalog.CatalogSeeder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BookVerse", appName)
  }

  @Test
  fun `verify catalog seeder generates over 1000 items`() {
    val items = CatalogSeeder.getExpansivePublicDomainCatalog()
    assertTrue("Catalog should contain at least 1000 items, found: ${items.size}", items.size >= 1000)
  }
}

