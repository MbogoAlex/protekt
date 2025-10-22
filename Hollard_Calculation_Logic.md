# Hollard Premium Calculation Logic

## **🔍 Understanding the Fields**

### **Input Fields (From LoanContract & ProductProperties)**

| Field | Source | Description | Example |
|-------|---------|-------------|---------|
| **SUM ASSURED** | `loanContract.getPrincipal()` | The loan principal amount being insured | 10,000.00 |
| **PREMIUM RATE** | ProductProperty "PREMIUM_RATE" | Hollard's insurance rate (as decimal) | 0.0045 (0.45%) |
| **LEVY RATE** | ProductProperty "LEVY_RATE" | Government tax rate | 0.05 (5%) |
| **ADMIN FEE RATE** | ProductProperty "ADMIN_FEE_RATE" | Hollard's administrative fee rate | 0.25 (25%) |

### **Calculated Fields (Stored in PremiumCalculation)**

| Field | Calculation | Description | Example |
|-------|-------------|-------------|---------|
| **baseAmount** | `sumAssured` | The amount insurance premium is calculated on | "10000.00" |
| **grossPremium** | `sumAssured × premiumRate` | Raw premium before any deductions | "45.00" |
| **taxAmount** | `grossPremium × levyRate` | Government levy/tax amount | "2.25" |
| **netPremium** | `grossPremium - taxAmount` | Premium after tax deduction (Net IPL) | "42.75" |
| **adminFeeAmount** | `netPremium × adminFeeRate` | Hollard's administrative fee | "10.69" |
| **totalPremium** | `netPremium - adminFeeAmount` | Final amount customer pays | "32.06" |

---

## **🧮 Step-by-Step Calculation Logic**

### **Step 1: Get Base Amount**
```java
BigDecimal sumAssured = policy.getLoanContract().getPrincipal();
// Result: 10,000.00 (the loan principal being insured)
```

### **Step 2: Calculate GROSS PREMIUM**
```java
BigDecimal grossPremium = sumAssured.multiply(premiumRate);
// Calculation: 10,000.00 × 0.0045 = 45.00
// This is Hollard's base premium before any fees/taxes
```

### **Step 3: Calculate LEVY (Government Tax)**
```java
BigDecimal levyAmount = grossPremium.multiply(levyRate);
// Calculation: 45.00 × 0.05 = 2.25
// This is government tax that must be deducted
```

### **Step 4: Calculate Net IPL (After Tax)**
```java
BigDecimal netIPL = grossPremium.subtract(levyAmount);
// Calculation: 45.00 - 2.25 = 42.75
// This is the premium amount after government tax
```

### **Step 5: Calculate ADMIN FEE (Hollard's Fee)**
```java
BigDecimal adminFeeAmount = netIPL.multiply(adminFeeRate);
// Calculation: 42.75 × 0.25 = 10.69
// This is Hollard's administrative/processing fee
```

### **Step 6: Calculate FINAL PREMIUM**
```java
BigDecimal finalPremium = netIPL.subtract(adminFeeAmount);
// Calculation: 42.75 - 10.69 = 32.06
// This is what the customer actually pays
```

---

## **💰 Money Flow Explanation**

Think of it like this:

### **The Premium Journey:**
```
Customer's Loan Principal (10,000)
         ↓
Hollard calculates 0.45% premium = 45.00
         ↓
Government takes 5% tax = -2.25
         ↓
Remaining for Hollard = 42.75
         ↓
Hollard takes 25% admin fee = -10.69
         ↓
Final customer payment = 32.06
```

### **Who Gets What:**
- **Government**: 2.25 (levy/tax)
- **Hollard**: 10.69 (admin fee) + remaining profit
- **Customer Pays**: 32.06 (total)

---

## **🔧 Technical Implementation**

### **Where Each Field is Stored:**

```java
PremiumCalculation calculation = PremiumCalculation.builder()
    .baseAmount("10000.00")        // sumAssured (what premium is calculated on)
    .premiumRate("0.0045")         // Hollard's rate (0.45%)
    .grossPremium("45.00")         // Base premium before deductions
    .taxRate("0.05")               // Government levy rate (5%)
    .taxAmount("2.25")             // Government levy amount
    .netPremium("42.75")           // After tax (Net IPL)
    .adminFeeRate("0.25")          // Hollard admin rate (25%)
    .adminFeeAmount("10.69")       // Hollard admin fee amount
    .totalPremium("32.06")         // Final customer payment
    .calculationMethod("HOLLARD_STANDARD")
    .build();
```

### **Configuration via ProductProperties:**

```bash
# Set up Hollard rates
POST /api/v1/products/product-property
{"key": "PREMIUM_RATE", "value": "0.0045"}     # 0.45%

POST /api/v1/products/product-property
{"key": "LEVY_RATE", "value": "0.05"}          # 5%

POST /api/v1/products/product-property
{"key": "ADMIN_FEE_RATE", "value": "0.25"}     # 25%
```

---

## **📊 Real-World Example**

### **Scenario**: Customer wants loan insurance

1. **Customer gets loan**: K10,000 principal
2. **Customer chooses Hollard insurance**: System looks up Hollard product
3. **System auto-calculates**:
   - Base: K10,000 (loan principal)
   - Hollard premium: K10,000 × 0.45% = K45.00
   - Government tax: K45.00 × 5% = K2.25
   - After tax: K45.00 - K2.25 = K42.75
   - Hollard fee: K42.75 × 25% = K10.69
   - **Customer pays: K42.75 - K10.69 = K32.06**

4. **Money distribution**:
   - Government gets: K2.25
   - Hollard gets: K10.69 + profit margin
   - Coverage: K10,000 (if customer defaults, Hollard pays the loan)

---

## **🎯 Key Business Logic Points**

### **Why This Calculation Makes Sense:**

1. **Base on Principal**: Insurance covers the loan amount, so premium calculated on principal
2. **Government Levy**: Insurance is regulated, so government takes tax first
3. **Admin Fee**: Hollard needs to cover operational costs
4. **Final Amount**: What customer actually pays for the insurance

### **Flexibility:**

- **Different Providers**: Change rates via ProductProperties
- **Rate Updates**: Hollard changes admin fee? Update property, no code changes
- **New Providers**: Turaco uses different formula? Add new calculation method

This structure perfectly matches Hollard's business model and can easily adapt to other insurance providers!

---

## **🔄 API Response Example**

When creating a policy with Hollard product, the response will include the complete breakdown:

```json
{
  "policy_id": 123,
  "customer_name": "John Doe",
  "product_name": "Hollard Credit Life Insurance",
  "loan_amount": "10000.00",
  "premium_value": "32.06",
  "premium_calculation": {
    "base_amount": "10000.00",
    "premium_rate": "0.0045",
    "gross_premium": "45.00",
    "tax_rate": "0.05",
    "tax_amount": "2.25",
    "net_premium": "42.75",
    "admin_fee_rate": "0.25",
    "admin_fee_amount": "10.69",
    "total_premium": "32.06",
    "calculation_method": "HOLLARD_STANDARD"
  }
}
```

This provides complete transparency to both customers and internal teams about how the premium was calculated.