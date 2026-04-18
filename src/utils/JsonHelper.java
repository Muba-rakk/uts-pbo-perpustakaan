package utils;

import com.google.gson.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {

  // Membuat instance Gson yang sudah dikonfigurasi dengan LocalDateAdapter
  private static final Gson gson = new GsonBuilder()
      .setPrettyPrinting()
      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
      .create();

  // Menyimpan List objek ke dalam file JSON
  public static <T> void saveListToJson(List<T> list, String filePath) {
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(list, writer);
    } catch (IOException e) {
      System.out.println("Gagal menyimpan data ke " + filePath + ": " + e.getMessage());
    }
  }

  // Membaca List objek dari file JSON
  public static <T> List<T> loadListFromJson(String filePath, Type type) {
    try (FileReader reader = new FileReader(filePath)) {
      List<T> list = gson.fromJson(reader, type);
      if (list == null) {
        return new ArrayList<>();
      }
      return list;
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }

  // Inner class untuk menterjemahkan LocalDate <-> String untuk Gson
  private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(date.format(formatter));
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      return LocalDate.parse(json.getAsString(), formatter);
    }
  }
}
