package com.netflix.graphql.dgs.codegen.cases.projectionWithType.expected

import kotlin.String

public object DgsConstants {
  public const val QUERY_TYPE: String = "Query"

  public object EMPLOYEE {
    public const val TYPE_NAME: String = "Employee"

    public const val Firstname: String = "firstname"

    public const val Company: String = "company"
  }

  public object QUERY {
    public const val TYPE_NAME: String = "Query"

    public const val Person: String = "person"

    public const val People: String = "people"
  }

  public object PERSON {
    public const val TYPE_NAME: String = "Person"

    public const val Firstname: String = "firstname"
  }
}
