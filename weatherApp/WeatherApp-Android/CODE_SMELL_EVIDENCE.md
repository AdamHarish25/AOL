
### 7.2 Bukti Code Smells (Code Snippets)

Berikut adalah cuplikan kode asli (sebelum refactoring) yang menunjukkan adanya Code Smells yang diidentifikasi di Bab 1.2.

#### 1. The Bloater - God Class / Large Class
**File:** `HomeActivity.java`
**Analisis:** Activity ini melakukan inisialisasi permission lokasi, akses GPS, logika request HTTP (Volley), hingga parsing JSON manual.

```java
public class HomeActivity extends AppCompatActivity {
    // Variable global campur aduk (UI, Data, Permission)
    private final int WEATHER_FORECAST_APP_UPDATE_REQ_CODE = 101;
    private static final int PERMISSION_CODE = 1;

    // ... (ratusan baris kode bercampur)

    private void getDataUsingNetwork() {
        // Logika akses GPS langsung di Activity
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        
        // Logika cek permission manual di Activity
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ...) {
             ActivityCompat.requestPermissions(...);
        }
        
        // Logika request Network langsung di Activity
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ...);
        requestQueue.add(jsonObjectRequest);
    }
}
```

#### 2. The Bloater - Long Method
**File:** `HomeActivity.java`
**Analisis:** Method `getTodayWeatherInfo` terlalu panjang dan melakukan parsing JSON level rendah yang sulit dibaca.

```java
@SuppressLint("DefaultLocale")
private void getTodayWeatherInfo(String name) {
    // ... deklarasi object request, antrian, dll ...
    
    // Callback response handling yang sangat panjang
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(..., response -> {
        try {
            // Parsing satu per satu, sangat rentan error dan sulit dibaca
            update_time = response.getJSONObject("current").getLong("dt");
            condition = response.getJSONArray("daily").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getInt("id");
            sunrise = response.getJSONArray("daily").getJSONObject(0).getLong("sunrise");
            sunset = response.getJSONArray("daily").getJSONObject(0).getLong("sunset");
            description = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("main");
            // ... (lanjut puluhan baris parsing lainnya)
            
            updateUI(); // Baru update UI setelah parsing selesai
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }, null);
    
    requestQueue.add(jsonObjectRequest);
}
```

#### 3. Hardcoded Values
**File:** `Toaster.java`
**Analisis:** Menggunakan string Hex Color secara langsung. Jika desainer ingin mengubah tema aplikasi, developer harus mencari manual string ini di kode Java, bukan di `colors.xml`.

```java
public class Toaster {
    public static void successToast(Context context, String msg) {
        Toasty.custom(
                context,
                msg,
                R.drawable.ic_baseline_check_24,
                "#454B54", // <-- HARDCODED COLOR: Seharusnya pakai R.color.blockBGColor
                14,
                "#EEEEEE"); // <-- HARDCODED COLOR: Seharusnya pakai R.color.textColor
    }
}
```

#### 4. The Dispensable - Lazy Class / Dead Code
**File:** `CityFinder.java` (File ini dihapus saat refactoring)
**Analisis:** Kelas ini hanya memiliki 1 method statis yang sebenarnya bisa digabung ke helper lain atau class yang lebih relevan.

```java
public class CityFinder {
    // Kelas ini hanya jadi "wrapper" untuk satu fungsi ini saja.
    // Tidak menyimpan state, tidak punya tanggung jawab jelas.
    public static void setLongitudeLatitude(Location location) {
        try {
            LocationCord.lat = String.valueOf(location.getLatitude());
            LocationCord.lon = String.valueOf(location.getLongitude());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
```

#### 5. Improper Instantiation
**File:** `HomeActivity.java` & `URL.java`
**Analisis:** Membuat instance objek `new URL()` padahal method di dalamnya bisa (dan seharusnya) static. Pemborosan memory (walau kecil) dan membingungkan secara semantic.

```java
// Di HomeActivity.java
private void getTodayWeatherInfo(String name) {
    URL url = new URL(); // <-- Improper Instantiation: Buat objek cuma buat panggil getter
    // ...
    // Di URL.java (Sebelum Refactor)
    public String getLink() { // Method instance, bukan static
        return "https://api.openweathermap.org/..."
    }
}
```
