package com.meditrack;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;
@RestController @RequestMapping("/api") public class ApiController {
 private final JdbcTemplate db; public ApiController(JdbcTemplate db){this.db=db;}
 @GetMapping("/medicines") public List<Map<String,Object>> medicines(){return db.queryForList("SELECT *, CAST(stock AS DOUBLE PRECISION)/NULLIF(per_dose * (LENGTH(times)-LENGTH(REPLACE(times,',',''))+1),0) AS days_left FROM medicines ORDER BY id DESC");}
 
@PostMapping("/medicines")
public ResponseEntity<?> add(@RequestBody Map<String, Object> p) {
    try {
        String name = str(p, "name");
        String dose = str(p, "dose");
        String times = str(p, "times");

        String condition = str(p, "condition");
        String frequency = str(p, "frequency");
        String unitType = str(p, "unitType");

        int stock = num(p, "stock");
        int perDose = num(p, "perDose");
        int threshold = num(p, "threshold");
        int alertStock = num(p, "alertStock");

        if (name.isBlank() || dose.isBlank()
                || !times.matches("([01]\\d|2[0-3]):[0-5]\\d(,([01]\\d|2[0-3]):[0-5]\\d)*")
                || stock < 0 || perDose < 1
                || threshold < 0 || alertStock < 0) {

            return ResponseEntity.badRequest()
                    .body("Please check the medicine details.");
        }

        db.update(
            "INSERT INTO medicines " +
            "(name, dose, times, stock, per_dose, threshold, " +
            "condition_name, frequency, unit_type, alert_stock) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            name, dose, times, stock, perDose, threshold,
            condition, frequency, unitType, alertStock
        );

        return ResponseEntity.ok(Map.of("saved", true));

    } catch (Exception e) {
        return ResponseEntity.badRequest()
                .body("Invalid medicine details: " + e.getMessage());
    }
}

 @PostMapping("/medicines/{id}/taken") public ResponseEntity<?> taken(@PathVariable long id){int changed=db.update("UPDATE medicines SET stock=stock-per_dose WHERE id=? AND stock>=per_dose",id);if(changed==0)return ResponseEntity.badRequest().body("Medicine not found or insufficient stock");db.update("INSERT INTO dose_logs(medicine_id) VALUES(?)",id);return ResponseEntity.ok(Map.of("saved",true));}
 @PostMapping("/medicines/{id}/restock") public ResponseEntity<?> restock(@PathVariable long id,@RequestBody Map<String,Object> p){try{int qty=num(p,"quantity");if(qty<=0)return ResponseEntity.badRequest().body("Quantity must be positive");int n=db.update("UPDATE medicines SET stock=stock+? WHERE id=?",qty,id);return n>0?ResponseEntity.ok(Map.of("saved",true)):ResponseEntity.notFound().build();}catch(Exception e){return ResponseEntity.badRequest().body("Invalid quantity");}}
 @DeleteMapping("/medicines/{id}") public void delete(@PathVariable long id){db.update("DELETE FROM dose_logs WHERE medicine_id=?",id);db.update("DELETE FROM medicines WHERE id=?",id);}
 
@GetMapping("/dose-logs")
public List<Map<String, Object>> doseLogs() {
   return db.queryForList("SELECT *, CAST(stock AS DOUBLE PRECISION)/NULLIF(per_dose * (LENGTH(times)-LENGTH(REPLACE(times,',',''))+1),0) AS days_left FROM medicines ORDER BY id DESC");
}
@GetMapping("/tests") public List<Map<String,Object>> tests(){return db.queryForList("SELECT * FROM blood_tests ORDER BY next_due");}
 @PostMapping("/tests") public ResponseEntity<?> addTest(@RequestBody Map<String,Object> p){String name=str(p,"name"),date=str(p,"date"),result=str(p,"result"),due=str(p,"due");if(name.isBlank()||!date.matches("\\d{4}-\\d{2}-\\d{2}")||!due.matches("\\d{4}-\\d{2}-\\d{2}"))return ResponseEntity.badRequest().body("Enter a test name and valid dates");db.update("INSERT INTO blood_tests(name,test_date,result,next_due) VALUES(?,?,?,?)",name,date,result,due);return ResponseEntity.ok(Map.of("saved",true));}
 private String str(Map<String,Object> p,String k){return String.valueOf(p.getOrDefault(k,"")).trim();} private int num(Map<String,Object> p,String k){return Integer.parseInt(str(p,k));}
}
