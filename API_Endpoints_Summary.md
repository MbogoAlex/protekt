# Protekt Spring API Endpoints Summary

## Overview
This document summarizes all the available API endpoints in the enhanced Protekt Spring application, including the new provider and premium calculation endpoints.

## Base URL
```
/api/v1/products
```

---

## **Product Management**

### Create Product
- **POST** `/api/v1/products`
- **Body**: `ProductCreationDto`
- **Response**: Created product details

### Update Product
- **PUT** `/api/v1/products`
- **Body**: `ProductUpdateDto`
- **Response**: Updated product details

### Get Product by ID
- **GET** `/api/v1/products/{id}`
- **Response**: Product details

### Filter Products
- **GET** `/api/v1/products`
- **Query Parameters**:
  - `provider` (optional)
  - `name` (optional)
  - `product_beneficiary_type` (optional)
  - `created_at_start_date` (optional)
  - `created_at_end_date` (optional)
  - `page` (optional)
  - `page_size` (optional)
- **Response**: Paginated list of products

---

## **Product Terms Management**

### Create Product Term
- **POST** `/api/v1/products/terms`
- **Body**: `ProductTermCreationDto`
- **Response**: Created product term

### Update Product Term
- **PUT** `/api/v1/products/terms`
- **Body**: `ProductTermUpdateDto`
- **Response**: Updated product term

### Get Product Term by ID
- **GET** `/api/v1/products/terms/{id}`
- **Response**: Product term details

### Filter Product Terms
- **GET** `/api/v1/products/terms`
- **Query Parameters**:
  - `product_id` (optional)
  - `created_at_start_date` (optional)
  - `created_at_end_date` (optional)
  - `page` (optional)
  - `page_size` (optional)
- **Response**: Paginated list of product terms

---

## **Product Policies Management**

### Create Product Policy
- **POST** `/api/v1/products/policies`
- **Body**: `ProductPolicyCreationDto`
- **Response**: Created product policy

### Update Product Policy
- **PUT** `/api/v1/products/policies`
- **Body**: `ProductPolicyUpdateDto`
- **Response**: Updated product policy

### Get Product Policy by ID
- **GET** `/api/v1/products/policies/{id}`
- **Response**: Product policy details

### Filter Product Policies
- **GET** `/api/v1/products/policies`
- **Query Parameters**:
  - `product_id` (optional)
  - `loan_id` (optional)
  - `customer_id` (optional)
  - `customer_name` (optional)
  - `created_at_start_date` (optional)
  - `created_at_end_date` (optional)
  - `policy_start_date` (optional)
  - `policy_end_date` (optional)
  - `page` (optional)
  - `page_size` (optional)
- **Response**: Paginated list of product policies

---

## **Product Properties Management**

### Create Product Property
- **POST** `/api/v1/products/product-property`
- **Body**: `ProductPropertyDto`
- **Response**: Created product property

### Update Product Property
- **PUT** `/api/v1/products/product-property`
- **Body**: `ProductPropertyDto`
- **Response**: Updated product property

### Get Product Property by ID
- **GET** `/api/v1/products/product-property/{id}`
- **Response**: Product property details

### Get Properties by Product ID
- **GET** `/api/v1/products/product-property/pid/{productId}`
- **Response**: List of product properties

---

## **🆕 Provider Management**

### Create Provider
- **POST** `/api/v1/products/providers`
- **Body**: `ProviderCreationDto`
```json
{
  "name": "Hollard Assurance",
  "code": "HOLLARD",
  "description": "Hollard Insurance Provider",
  "contact_email": "contact@hollard.co.zm",
  "api_endpoint": "https://api.hollard.com",
  "is_active": true
}
```
- **Response**: Created provider details

### Update Provider
- **PUT** `/api/v1/products/providers`
- **Body**: `ProviderDto`
- **Response**: Updated provider details

### Get Provider by ID
- **GET** `/api/v1/products/providers/{id}`
- **Response**: Provider details

### Get Provider by Code
- **GET** `/api/v1/products/providers/code/{code}`
- **Example**: `/api/v1/products/providers/code/HOLLARD`
- **Response**: Provider details

### Filter Providers
- **GET** `/api/v1/products/providers`
- **Query Parameters**:
  - `name` (optional)
  - `code` (optional)
  - `is_active` (optional)
  - `page` (optional)
  - `page_size` (optional)
- **Response**: Paginated list of providers

---

## **🆕 Premium Calculation**

### Calculate Premium for Policy
- **POST** `/api/v1/products/policies/{policyId}/calculate-premium`
- **Response**: Detailed premium calculation breakdown
```json
{
  "id": 1,
  "product_policy_id": 123,
  "base_amount": "13500.00",
  "premium_rate": "0.0045",
  "gross_premium": "60.75",
  "tax_rate": "0.05",
  "tax_amount": "3.04",
  "admin_fee_rate": "0.25",
  "admin_fee_amount": "14.43",
  "total_premium": "43.28",
  "calculation_method": "HOLLARD_STANDARD"
}
```

### Recalculate Premium for Policy
- **POST** `/api/v1/products/policies/{policyId}/recalculate-premium`
- **Response**: New premium calculation (keeps history)

### Get Premium Calculation History
- **GET** `/api/v1/products/policies/{policyId}/premium-calculations`
- **Response**: List of all premium calculations for the policy (newest first)

---

## **Sample Usage Scenarios**

### 1. Setting up Hollard Provider
```bash
# Create Hollard provider
POST /api/v1/products/providers
{
  "name": "Hollard Assurance",
  "code": "HOLLARD",
  "description": "Monthly Standard Credit Life Assurance",
  "contact_email": "fanaka@hollard.co.zm",
  "is_active": true
}
```

### 2. Creating Hollard Product with Properties
```bash
# Create product
POST /api/v1/products
{
  "name": "Hollard Credit Life Assurance",
  "provider": "HOLLARD",
  "description": "Monthly Standard Credit Life Assurance",
  "policy_duration": 1,
  "policy_duration_type": "MONTHS",
  "premium_calculation_method": "HOLLARD_STANDARD",
  "requires_complex_calculation": true
}

# Add Hollard-specific properties
POST /api/v1/products/product-property
{
  "key": "PREMIUM_RATE",
  "value": "0.0045",
  "product_id": 1
}

POST /api/v1/products/product-property
{
  "key": "TAX_RATE",
  "value": "0.05",
  "product_id": 1
}

POST /api/v1/products/product-property
{
  "key": "ADMIN_FEE_RATE",
  "value": "0.25",
  "product_id": 1
}
```

### 3. Calculate Premium for Policy
```bash
# Calculate premium using Hollard formula
POST /api/v1/products/policies/123/calculate-premium

# Response will show breakdown:
# Base Amount: 13,500
# Premium Rate: 0.45%
# Gross Premium: 60.75
# Tax (5%): 3.04
# Admin Fee (25%): 14.43
# Final Premium: 43.28
```

### 4. Provider Management
```bash
# Get all active providers
GET /api/v1/products/providers?is_active=true

# Find provider by code
GET /api/v1/products/providers/code/HOLLARD

# Filter providers by name
GET /api/v1/products/providers?name=hollard
```

---

## **Error Handling**

All endpoints return consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "errors": {
    "general": "Specific error details"
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

## **Response Format**

All successful responses follow this format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* Response data */ },
  "pagination": { /* For paginated responses */ },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

This enhanced API now fully supports multi-provider insurance management with complex premium calculations while maintaining backward compatibility with existing functionality.