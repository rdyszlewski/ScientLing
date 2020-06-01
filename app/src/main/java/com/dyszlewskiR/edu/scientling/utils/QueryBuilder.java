package com.dyszlewskiR.edu.scientling.utils;

/**
 * Created by Razjelll on 20.01.2017.
 */

public class QueryBuilder {
    public static String build(String statement, String selection,
                               String groupBy, String having, String orderBy, String limit) {
        StringBuilder queryBuilder = new StringBuilder(statement);
        if (selection != null) {
            queryBuilder.append(" WHERE ").append(selection);
        }
        if (groupBy != null) {
            queryBuilder.append(" GROUP BY ").append(groupBy);
        }
        if (having != null) {
            queryBuilder.append(" HAVING ").append(having);
        }
        if (orderBy != null) {
            queryBuilder.append(" ORDER BY ").append(orderBy);
        }
        if (limit != null) {
            queryBuilder.append(" LIMIT ").append(limit);
        }
        return queryBuilder.toString();
    }
}
