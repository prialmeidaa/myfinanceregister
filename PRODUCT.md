# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

An individual who wants to organize and track their personal finances.

## Product Purpose

My Finance Register centralizes the recording of financial transactions and the maintenance of the data used to organize them: accounts and categories.

## Positioning

A straightforward web system for recording personal financial transactions and classifying those transactions by account and category.

## Operating Context

Frequent use for operational tasks: viewing transactions by period, creating and editing records, removing transactions, keeping accounts active or inactive, and organizing categories in a hierarchical structure.

## Capabilities and Constraints

- Recording financial transactions with a description, date, account, category, and amounts.
- Viewing the statement by month.
- Creating, editing, and deleting transactions.
- Creating and editing accounts.
- Activating and deactivating accounts.
- Creating, editing, and deleting categories hierarchically.
- Vaadin Flow 25.3.2 web application served by Spring Boot 4.1.1.
- H2 data during local execution, with no persistence between restarts.
- The project requires JDK 21.

## Brand Commitments

- The official name is "My Finance Register".
- The existing logo at `src/main/resources/static/images/logo-192x192.png` remains the official asset.
- The product must continue to be an operational interface with clear hierarchy and predictable actions.

## Evidence on Hand

- Existing logo: `src/main/resources/static/images/logo-192x192.png`.
- Navigation: Home, Accounts, Categories, and Statement.
- Vaadin implementation in Kotlin and the Lumo/Aura theme.

## Product Principles

- Scanability over decoration.
- Familiar conventions for recurring financial tasks.
- Clear, unambiguous financial states.
- Visual hierarchy that differentiates positive, negative, and neutral amounts.
- Consistency across listings, forms, and confirmations.

## Accessibility & Inclusion

No specific requirements beyond the native accessibility expected from Vaadin components.
