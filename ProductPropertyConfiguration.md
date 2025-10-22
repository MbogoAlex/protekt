# Product Property Configuration Guide

## Overview
This guide shows how to configure provider-specific properties using the existing `ProductProperty` entity to accommodate different insurance providers like Hollard, Turaco, etc.

## Hollard Insurance Configuration

For Hollard's "Monthly Standard Credit Life Assurance" product, configure the following properties:

### Core Calculation Properties

| Key | Value | Description |
|-----|-------|-------------|
| `PREMIUM_RATE` | `0.0045` | Premium rate (0.45%) |
| `PREMIUM_RATE_TYPE` | `PERCENTAGE` | Indicates the value type |
| `TAX_RATE` | `0.05` | Tax/Levy rate (5%) |
| `TAX_RATE_TYPE` | `PERCENTAGE` | Indicates the value type |
| `ADMIN_FEE_RATE` | `0.25` | Administrative fee rate (25%) |
| `ADMIN_FEE_TYPE` | `PERCENTAGE` | Indicates the value type |

### Calculation Method
| Key | Value | Description |
|-----|-------|-------------|
| `CALCULATION_METHOD` | `HOLLARD_STANDARD` | Identifies the calculation approach |
| `COMPLEX_CALCULATION` | `true` | Requires multi-step calculation |

### Provider Information
| Key | Value | Description |
|-----|-------|-------------|
| `PROVIDER_SCHEME_NAME` | `FANAKA TECHNOLOGIES LIMITED` | Scheme name with provider |
| `REPORTING_MONTH` | `OCTOBER` | Current reporting period |

## Example ProductProperty Records for Hollard

```sql
-- For Product ID 1 (Hollard Credit Life Assurance)
INSERT INTO protekt_product_properties (protekt_product_id, key, value, created_at, updated_at) VALUES
(1, 'PREMIUM_RATE', '0.0045', NOW(), NOW()),
(1, 'PREMIUM_RATE_TYPE', 'PERCENTAGE', NOW(), NOW()),
(1, 'TAX_RATE', '0.05', NOW(), NOW()),
(1, 'TAX_RATE_TYPE', 'PERCENTAGE', NOW(), NOW()),
(1, 'ADMIN_FEE_RATE', '0.25', NOW(), NOW()),
(1, 'ADMIN_FEE_TYPE', 'PERCENTAGE', NOW(), NOW()),
(1, 'CALCULATION_METHOD', 'HOLLARD_STANDARD', NOW(), NOW()),
(1, 'COMPLEX_CALCULATION', 'true', NOW(), NOW());
```

## Turaco Configuration Example

For a different provider like Turaco:

| Key | Value | Description |
|-----|-------|-------------|
| `PREMIUM_RATE` | `0.02` | Different premium rate (2%) |
| `PREMIUM_RATE_TYPE` | `PERCENTAGE` | Value type |
| `SERVICE_FEE` | `500` | Fixed service fee |
| `SERVICE_FEE_TYPE` | `FIXED` | Fixed amount |
| `CALCULATION_METHOD` | `TURACO_TIERED` | Tiered calculation |

## Internal/No Provider Configuration

For internal products with no external provider:

| Key | Value | Description |
|-----|-------|-------------|
| `PREMIUM_RATE` | `0.01` | Simple premium rate (1%) |
| `PREMIUM_RATE_TYPE` | `PERCENTAGE` | Value type |
| `CALCULATION_METHOD` | `INTERNAL_SIMPLE` | Simple calculation |
| `COMPLEX_CALCULATION` | `false` | No complex calculation needed |

## How the System Uses These Properties

### 1. Premium Calculation Process
```java
// The PremiumCalculationHelper reads these properties
Map<String, String> properties = getProductPropertiesMap(product);
BigDecimal premiumRate = getDecimalProperty(properties, "PREMIUM_RATE", "0.01");
String calculationMethod = properties.get("CALCULATION_METHOD");
```

### 2. Provider-Specific Logic
```java
// Based on the provider field in Product entity
if ("HOLLARD".equalsIgnoreCase(provider)) {
    return calculateHollardPremium(policy); // Uses Hollard properties
} else if ("TURACO".equalsIgnoreCase(provider)) {
    return calculateTuracoPremium(policy);  // Uses Turaco properties
}
```

### 3. Flexible Property Types
The system supports different property types through companion `_TYPE` properties:
- `PERCENTAGE`: Decimal values like 0.05 for 5%
- `FIXED`: Fixed amounts like 500 for K500
- `BOOLEAN`: True/false values
- `STRING`: Text values

## Benefits of This Approach

1. **Flexibility**: Each provider can define custom properties
2. **No Code Changes**: Adding new providers doesn't require code modifications
3. **Dynamic Configuration**: Properties can be updated without system restart
4. **Type Safety**: Property types are explicitly defined
5. **Backward Compatibility**: Existing products continue to work

## Sample API Usage

### Creating a Hollard Product with Properties
```json
{
  "name": "Hollard Credit Life Assurance",
  "provider": "HOLLARD",
  "description": "Monthly Standard Credit Life Assurance",
  "policy_duration": 1,
  "policy_duration_type": "MONTHS",
  "premium_calculation_method": "HOLLARD_STANDARD",
  "requires_complex_calculation": true,
  "product_properties": [
    {"key": "PREMIUM_RATE", "value": "0.0045"},
    {"key": "PREMIUM_RATE_TYPE", "value": "PERCENTAGE"},
    {"key": "TAX_RATE", "value": "0.05"},
    {"key": "TAX_RATE_TYPE", "value": "PERCENTAGE"},
    {"key": "ADMIN_FEE_RATE", "value": "0.25"},
    {"key": "ADMIN_FEE_TYPE", "value": "PERCENTAGE"}
  ]
}
```

This configuration approach ensures your system can accommodate any insurance provider's specific requirements while maintaining a clean, extensible architecture.