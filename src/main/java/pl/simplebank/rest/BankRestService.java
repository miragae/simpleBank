package pl.simplebank.rest;

import pl.simplebank.dao.AccountDaoLocal;
import pl.simplebank.dao.BankDaoLocal;
import pl.simplebank.dao.OperationDaoLocal;
import pl.simplebank.model.Account;
import pl.simplebank.model.Bank;
import pl.simplebank.model.Operation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static pl.simplebank.model.OperationType.TRANSFER;

@Path("/bank")
@RequestScoped
public class BankRestService {

    @Inject
    private AccountDaoLocal accountDao;
    @Inject
    private BankDaoLocal bankDao;
    @Inject
    private OperationDaoLocal operationDao;

    @GET
    @Path("/{accountNumber}")
    public Response checkIfAccountExists(@PathParam("accountNumber") String accountNumber, @Context HttpServletRequest request) {
        Account account = accountDao.findByNumber(accountNumber);
        if (account != null) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @GET
    @Path("/{accountNumberTo}/{accountNumberFrom}/{amount}")
    public Response acceptTransfer(
            @PathParam("accountNumberTo") String accountNumberTo,
            @PathParam("accountNumberFrom") String accountNumberFrom,
            @PathParam("amount") String amount,
            @Context HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        int port = request.getRemotePort();
        Bank bank = bankDao.findByIp(ip);
        Account accountTo = accountDao.findByNumber(accountNumberTo);
        Account accountFrom = accountDao.findByNumber(accountNumberFrom);
        if (bank != null && accountTo != null) {
            if (accountFrom == null) {
                accountFrom = new Account();
                accountFrom.setNumber(accountNumberFrom);
                accountFrom.setBank(bank);
                accountDao.save(accountFrom);
            }
            Operation transfer = new Operation();
            transfer.setType(TRANSFER);
            transfer.setToAccount(accountTo);
            transfer.setFromAccount(accountFrom);
            transfer.setAmount(new BigDecimal(amount));
            operationDao.save(transfer);
            accountTo.setBalance(accountTo.getBalance().add(new BigDecimal(amount)));
            accountDao.update(accountTo);

            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}
