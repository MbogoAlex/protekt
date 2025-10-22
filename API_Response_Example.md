# Enhanced API Response with Premium Calculation Breakdown

## **🎯 ProductPolicy API Response**

When you create or retrieve a ProductPolicy, the response now includes the complete premium calculation breakdown.

### **API Endpoint:**
```bash
POST /api/v1/products/policies
GET /api/v1/products/policies/{id}
```

### **Complete Response Example:**

```json
{
  "success": true,
  "message": "Policy created successfully",
  "data": {
    "policy_id": 123,
    "product_name": "Hollard Credit Life Insurance",
    "product_id": 1,
    "customer_id": 456,
    "customer_name": "John Doe",
    "loan_id": 789,
    "loan_amount": "15000.00",
    "premium_percentage": "0.45",
    "premium_value": "32.06",
    "premium_calculations": [
      {
        "id": 1,
        "product_policy_id": 123,
        "base_amount": "10000.00",
        "premium_rate": "0.0045",
        "gross_premium": "45.00",
        "tax_rate": "0.05",
        "tax_amount": "2.25",
        "levy_rate": "0.05",
        "levy_amount": "2.25",
        "admin_fee_rate": "0.25",
        "admin_fee_amount": "10.69",
        "net_premium": "42.75",
        "total_premium": "32.06",
        "calculation_method": "HOLLARD_STANDARD",
        "provider_calculation_ref": null,
        "created_at": "2024-01-15T10:30:00Z",
        "updated_at": "2024-01-15T10:30:00Z"
      }
    ],
    "created_at": "2024-01-15T10:30:00Z",
    "updated_at": "2024-01-15T10:30:00Z",
    "policy_start_date": "2024-01-15T00:00:00Z",
    "policy_end_date": "2024-02-15T00:00:00Z"
  }
}
```

---

## **📊 Breakdown Explanation**

### **Policy Summary Fields:**
- **policy_id**: 123
- **premium_value**: "32.06" (Final amount customer pays)
- **loan_amount**: "15000.00" (Total loan amount)

### **Calculation Details:**
- **base_amount**: "10000.00" (Loan principal - what insurance covers)
- **gross_premium**: "45.00" (10,000 × 0.45% = 45.00)
- **tax_amount**: "2.25" (45.00 × 5% = 2.25 government levy)
- **net_premium**: "42.75" (45.00 - 2.25 = 42.75 after tax)
- **admin_fee_amount**: "10.69" (42.75 × 25% = 10.69 Hollard fee)
- **total_premium**: "32.06" (42.75 - 10.69 = 32.06 final payment)

---

## **🔄 Different Scenarios**

### **1. Hollard Product (Complex Calculation)**
```json
{
  "premium_value": "32.06",
  "premium_calculations": [
    {
      "calculation_method": "HOLLARD_STANDARD",
      "base_amount": "10000.00",
      "gross_premium": "45.00",
      "tax_amount": "2.25",
      "admin_fee_amount": "10.69",
      "total_premium": "32.06"
    }
  ]
}
```

### **2. Internal Product (Simple Calculation)**
```json
{
  "premium_value": "200.00",
  "premium_calculations": []
}
```
*Note: Simple products don't store detailed calculations, only final premium.*

### **3. After Recalculation (Multiple Calculations)**
```json
{
  "premium_value": "32.06",
  "premium_calculations": [
    {
      "id": 2,
      "calculation_method": "HOLLARD_STANDARD",
      "admin_fee_rate": "0.30",
      "total_premium": "29.93",
      "created_at": "2024-01-20T14:00:00Z"
    },
    {
      "id": 1,
      "calculation_method": "HOLLARD_STANDARD",
      "admin_fee_rate": "0.25",
      "total_premium": "32.06",
      "created_at": "2024-01-15T10:30:00Z"
    }
  ]
}
```
*Note: Array shows calculation history, newest first.*

---

## **💡 Benefits for Frontend/API Consumers**

### **1. Immediate Transparency**
- No need for separate API call to get calculation breakdown
- Everything available in single response

### **2. Complete Audit Trail**
- See all calculation attempts
- Track rate changes over time
- Compliance reporting ready

### **3. Customer Service Support**
- Customer asks "How was my premium calculated?"
- All details available in one place
- Can explain each component clearly

### **4. Business Intelligence**
- Calculate average admin fees
- Track government levy collections
- Analyze provider profitability

---

## **🔧 Technical Implementation**

### **Backend Changes:**
```java
// ProductPolicyDto now includes:
private List<PremiumCalculationDto> premiumCalculations;

// ProductMapper automatically includes:
.premiumCalculations(productPolicy.getPremiumCalculations() != null ?
    productPolicy.getPremiumCalculations().stream()
        .map(premiumCalculationMapper::toPremiumCalculationDto)
        .toList() : null)
```

### **Database Query:**
- Single query retrieves policy + calculations
- No N+1 query issues
- Calculations sorted by created_at DESC (newest first)

### **Performance:**
- Lazy loading prevents unnecessary calculation loading
- Only loaded when policy is retrieved
- Cached for subsequent requests

---

## **📋 API Consumer Usage**

### **Frontend Display:**
```javascript
// Show basic policy info
const policy = response.data;
console.log(`Premium: ${policy.premium_value}`);

// Show detailed breakdown if available
if (policy.premium_calculations && policy.premium_calculations.length > 0) {
    const calc = policy.premium_calculations[0]; // Latest calculation
    console.log(`Gross Premium: ${calc.gross_premium}`);
    console.log(`Government Tax: ${calc.tax_amount}`);
    console.log(`Admin Fee: ${calc.admin_fee_amount}`);
    console.log(`Final Premium: ${calc.total_premium}`);
}
```

### **Customer Statement Generation:**
```javascript
// Generate premium breakdown for customer
const breakdown = policy.premium_calculations[0];
const statement = `
Premium Calculation Breakdown:
- Insurance Coverage: ${breakdown.base_amount}
- Premium Rate: ${(parseFloat(breakdown.premium_rate) * 100).toFixed(2)}%
- Gross Premium: ${breakdown.gross_premium}
- Government Levy (5%): ${breakdown.tax_amount}
- Administrative Fee (25%): ${breakdown.admin_fee_amount}
- Your Premium: ${breakdown.total_premium}
`;
```

This enhanced response provides complete transparency and eliminates the need for multiple API calls to understand premium calculations!