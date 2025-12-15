# Laporan Refactoring Weather App

Laporan ini mendokumentasikan perubahan struktur kode (refactoring) yang telah dilakukan pada proyek Weather App untuk meningkatkan keterbacaan, perawatan (maintainability), dan pemisahan tanggung jawab (Separation of Concerns).

## Ringkasan Perubahan
Kode yang sebelumnya monolitik di dalam `HomeActivity` telah dipecah menjadi beberapa kelas terpisah berdasarkan fungsinya.

Refactoring utama yang dilakukan:
1.  **Extract Class (Location Management):** Memindahkan logika GPS dan lokasi ke `LocationManager`.
2.  **Extract Class (Network Layer):** Memindahkan logika pemanggilan API (Volley) ke `WeatherRepository`.

---

## 1. Abstraksi Logika Lokasi (`LocationManager`)

### Masalah Sebelumnya
Logika untuk meminta izin lokasi (`checkSelfPermission`), inisialisasi `FusedLocationProviderClient`, dan konversi koordinat ke nama kota (`Geocoder`) tercampur di dalam `HomeActivity`. Hal ini membuat Activity terlalu gemuk dan sulit dibaca.

### Solusi Refactoring
Dibuat kelas `LocationManager` yang khusus menangani semua urusan lokasi. Activity cukup memanggil fungsi `getLastLocation` dan menyediakan callback/listener.

### Cuplikan Kode

**File: `LocationManager.java`**
Kelas ini membungkus logika permission dan pengambilan lokasi.
```java
public class LocationManager {
    // ... inisialisasi FusedLocationProviderClient ...

    public void getLastLocation(Activity activity, final OnLocationListener listener) {
        // Cek permission terpusat di sini
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ...) {
            listener.onPermissionNeeded();
            return;
        }

        // Ambil lokasi terakhir
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
            if (location != null) {
                listener.onLocationFound(location);
            } else {
                listener.onLocationError(new Exception("Location is null"));
            }
        });
    }

    public String getCityName(Location location) {
         // Logika Geocoder untuk mengubah lat/lon menjadi nama kota
         // ...
    }
}
```

**Penggunaan di `HomeActivity.java`**
Activity menjadi lebih bersih:
```java
private void getDataUsingNetwork() {
    locationManager.getLastLocation(this, new LocationManager.OnLocationListener() {
        @Override
        public void onLocationFound(Location location) {
            // Logika UI saat lokasi ditemukan
            LocationCord.lat = String.valueOf(location.getLatitude());
            city = locationManager.getCityName(location);
            getTodayWeatherInfo(city);
        }
        // ... handling error dan permission ...
    });
}
```

---

## 2. Abstraksi Network Layer (`WeatherRepository`)

### Masalah Sebelumnya
Pemanggilan API ke OpenWeatherMap menggunakan *Volley* (`RequestQueue`, `JsonObjectRequest`) dilakukan langsung di activity. Ini melanggar prinsip *Single Responsibility* karena Activity seharusnya hanya mengurus UI, bukan detail cara mengambil data dari internet.

### Solusi Refactoring
Dibuat kelas `WeatherRepository` yang bertindak sebagai jembatan data. Semua URL endpoint dan konfigurasi request dipindahkan ke sini.

### Cuplikan Kode

**File: `WeatherRepository.java`**
```java
public class WeatherRepository {
    // ... setup RequestQueue ...

    public void getWeatherData(String lat, String lon, final OnWeatherDataListener listener) {
        String link = "https://api.openweathermap.org/data/2.5/onecall?..." + ...;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, link, null, response -> {
            listener.onSuccess(response); // Callback sukses
        }, listener::onError); // Callback error

        requestQueue.add(jsonObjectRequest);
    }
}
```

**Penggunaan di `HomeActivity.java`**
Activity tidak perlu tahu URL apa yang dipanggil atau library apa (Volley/Retrofit) yang dipakai:
```java
private void getTodayWeatherInfo(String name) {
    weatherRepository.getWeatherData(LocationCord.lat, LocationCord.lon,
        new WeatherRepository.OnWeatherDataListener() {
            @Override
            public void onSuccess(JSONObject response) {
                // Update UI dengan data JSON yang diterima
                update_time = response.getJSONObject("current").getLong("dt");
                // ... parsing data lainnya ...
                updateUI();
            }

            @Override
            public void onError(Exception e) {
                Toaster.errorToast(HomeActivity.this, "Error fetching weather data");
            }
        });
}
```

---

## 3. Kesimpulan

Refactoring ini menghasilkan kode yang:
*   **Modular:** Logika lokasi dan network terpisah dari UI.
*   **Reusable:** `WeatherRepository` dan `LocationManager` bisa digunakan ulang di Activity/Fragment lain jika aplikasi dikembangkan lebih lanjut.
*   **Testable:** Lebih mudah membuat unit test untuk `WeatherRepository` karena terpisah dari siklus hidup Android Activity.
