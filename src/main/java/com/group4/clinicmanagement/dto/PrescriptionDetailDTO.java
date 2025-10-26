package com.group4.clinicmanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PrescriptionDetailDTO {

    private int prescriptionDetailId;   // ID chi tiết toa thuốc
    private int prescriptionId;         // ID của toa thuốc (Prescription)
    private int medicineId;             // ID của thuốc (Medicine)
    private String medicineName;            // Tên thuốc (hiển thị dễ đọc)
    private String dosage;                  // Liều dùng (ví dụ: "500mg")
    private String usage;                   // Cách dùng (uống sáng / tối / sau ăn)
    private int quantity;               // Số lượng thuốc
    private int duration;               // Số ngày sử dụng
    private String instruction;                    // Ghi chú thêm (nếu có)
    private String frequency;
}