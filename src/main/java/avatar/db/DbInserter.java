/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avatar.db;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

/**
 *
 * @author kitakeyos - Hoàng Hữu Dũng
 */
public class DbInserter {
    
    private static final Logger logger = Logger.getLogger(DbInserter.class);

    private final QueryRunner queryRunner;

    public DbInserter(QueryRunner queryRunner) {
        this.queryRunner = queryRunner;
    }

    public <T> T insertResultAsObj(String sql, Object... params) {
        try {
            return this.queryRunner.insert(sql, new ScalarHandler<T>(), params);
        } catch (Exception e) {
            logger.error("insertResultAsObj() EXCEPTION: " + e.getMessage());
            return null;
        }
    }
}
