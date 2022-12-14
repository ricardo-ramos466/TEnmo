package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** DAO methods related to TEbucks transfers between users & transfer information requests */

@Component
public class JdbcTransferDao implements TransferDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final JdbcUserDao userDao = new JdbcUserDao();
    private final JdbcAccountDao accountDao = new JdbcAccountDao();

    private final String sqlStatement = "SELECT at.account_id, af.account_id, t.transfer_type_id, t.transfer_status_id, t.transfer_id, ts.transfer_status_desc, tt.transfer_type_desc, uf.username as from_user, ut.username as to_user, t.amount, at.user_id as to_id, af.user_id as from_id " +
            "FROM transfer t " +
            "JOIN transfer_status ts on ts.transfer_status_id = t.transfer_status_id " +
            "JOIN transfer_type tt on tt.transfer_type_id = t.transfer_type_id " +
            "JOIN account af on af.account_id = t.account_from " +
            "JOIN account at on at.account_id = t.account_to " +
            "JOIN tenmo_user uf on uf.user_id = af.user_id " +
            "JOIN tenmo_user ut on ut.user_id = at.user_id ";


    @Override
    public List<Transfer> findAll(User currentUser) {
        String sql = sqlStatement + "WHERE (af.user_id = ? OR at.user_id = ?) AND t.transfer_type_id = 2;";

        List<Transfer> list = new ArrayList<>();
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, currentUser.getId(), currentUser.getId());
        while (results.next()) {
            list.add(mapRowToTransfer(results));
        }
        return list;
    }

    public List<Transfer> findTransfer(String username, User currentUser) {
        SqlRowSet results;
        if (!username.equals(currentUser.getUsername())) {
            String sql = sqlStatement + "WHERE (uf.username = ? AND at.user_id = ? " +
                    "OR ut.username = ? AND af.user_id = ?) AND t.transfer_type_id = 2;";
            results = jdbcTemplate.queryForRowSet(sql, username, currentUser.getId(), username, currentUser.getId());
        } else {
            String sql = sqlStatement + "WHERE (uf.username = ? or ut.username = ?) AND t.transfer_type_id = 2;";
            results = jdbcTemplate.queryForRowSet(sql, username, username);
        }
        List<Transfer> list = new ArrayList<>();
        while (results.next()) {
            list.add(mapRowToTransfer(results));
        }
        return list;
    }

    @Override
    public List<Transfer> findTransfer(int id, User currentUser) {
        SqlRowSet results;
        if (id != currentUser.getId()) {
            String sql = sqlStatement + "WHERE (af.user_id = ? AND at.user_id = ? OR af.user_id = ? AND at.user_id = ?) AND t.transfer_type_id = 2;";
            results = jdbcTemplate.queryForRowSet(sql, id, currentUser.getId(), currentUser.getId(), id);
        } else {
            String sql = sqlStatement + "WHERE (af.user_id = ? OR at.user_id = ?) AND t.transfer_type_id = 2;";
            results = jdbcTemplate.queryForRowSet(sql, id, id);
        }
        List<Transfer> list = new ArrayList<>();
        while (results.next()) {
            list.add(mapRowToTransfer(results));
        }
        return list;
    }

    @Override
    public List<Transfer> findTransfer(String username, User currentUser, int isFrom) {
        SqlRowSet results;
        if (isFrom == 1) {
            if (!username.equals(currentUser.getUsername())) {
                String sql = sqlStatement + "WHERE at.user_id = ? AND uf.username = ? AND t.transfer_type_id = 2;";
                results = jdbcTemplate.queryForRowSet(sql, currentUser.getId(), username);
            } else {
                String sql = sqlStatement + "WHERE uf.username = ? AND t.transfer_type_id = 2;";
                results = jdbcTemplate.queryForRowSet(sql, username);
            }
            List<Transfer> list = new ArrayList<>();
            while (results.next()) {
                list.add(mapRowToTransfer(results));
            }
            return list;
        } else {
            if (!username.equals(currentUser.getUsername())) {
                String sql = sqlStatement + "WHERE ut.username = ? AND af.user_id = ? AND t.transfer_type_id = 2;";
                results = jdbcTemplate.queryForRowSet(sql, username, currentUser.getId());
            } else {
                String sql = sqlStatement + "WHERE ut.username = ? AND t.transfer_type_id = 2;";
                results = jdbcTemplate.queryForRowSet(sql, username);
            }
            List<Transfer> list = new ArrayList<>();
            while (results.next()) {
                list.add(mapRowToTransfer(results));
            }
            return list;
        }
    }

    @Override
    public List<Transfer> findTransfer(int id, User currentUser, int isFrom) {
        SqlRowSet results;
        if (isFrom == 1) {
            if (id != currentUser.getId()) {
                String sql = sqlStatement + "WHERE af.user_id = ? AND at.user_id = ? AND t.transfer_type_id = 2;";
                results = jdbcTemplate.queryForRowSet(sql, id, currentUser.getId());
            } else {
                String sql = sqlStatement + "WHERE af.user_id = ? AND t.transfer_type_id = 2;";
                results = jdbcTemplate.queryForRowSet(sql, id);
            }
            List<Transfer> list = new ArrayList<>();
            while (results.next()) {
                list.add(mapRowToTransfer(results));
            }
            return list;
        } else {
            if (id != currentUser.getId()) {
                String sql = sqlStatement + "WHERE (at.user_id = ? AND af.user_id = ?) AND t.transfer_type_id = 2;";
                results = jdbcTemplate.queryForRowSet(sql, id, currentUser.getId());
            } else {
                String sql = sqlStatement + "WHERE at.user_id = ? AND t.transfer_type_id = 2;";
                results = jdbcTemplate.queryForRowSet(sql, id);
            }
            List<Transfer> list = new ArrayList<>();
            while (results.next()) {
                list.add(mapRowToTransfer(results));
            }
            return list;
        }
    }

    @Override
    public Transfer findById(int transferId, User currentUser) {
        Transfer transfer = null;
        String sql = sqlStatement + "WHERE t.transfer_id = ? AND at.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId, currentUser);
        while (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public Transfer findById(int transferId) {
        Transfer transfer = null;
        String sql = sqlStatement + "WHERE t.transfer_id = ? " + ";";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        while (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    public void addTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?,?,?,?,?);";
        jdbcTemplate.update(sql, transfer.getType(), transfer.getStatus(), transfer.getFromAccountId(), transfer.getToAccountId(), transfer.getAmount());
    }

    public void updateTransfer(int statusId, Transfer transfer) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, statusId, transfer.getId());
    }

    public List<Transfer> viewPendingTransfers(User currentUser) {
        List<Transfer> transfers = new ArrayList<>();
        SqlRowSet results = null;
        String sql = sqlStatement + "WHERE at.user_id = ? OR af.user_id = ? AND t.transfer_type_id = 1;";
        results = jdbcTemplate.queryForRowSet(sql, currentUser.getId(), currentUser.getId());
        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;

    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setId(rs.getInt("transfer_id"));
        transfer.setToUserId(rs.getInt("to_id"));
        transfer.setFromUserId(rs.getInt("from_id"));
        transfer.setFromUserName(rs.getString("from_user"));
        transfer.setToUserName(rs.getString("to_user"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        transfer.setType(rs.getInt("transfer_type_id"));
        transfer.setStatus(rs.getInt("transfer_status_id"));
        return transfer;
    }
}
