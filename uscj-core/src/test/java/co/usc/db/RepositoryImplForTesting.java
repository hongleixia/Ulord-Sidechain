/*
 * This file is part of USC
 * Copyright (C) 2016 - 2018 USC developer team.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.usc.db;

import co.usc.config.TestSystemProperties;
import co.usc.core.UscAddress;
import co.usc.config.TestSystemProperties;
import co.usc.core.UscAddress;
import org.ethereum.core.AccountState;
import org.ethereum.db.ContractDetails;
import org.ethereum.vm.DataWord;

/**
 * Created by ajlopez on 08/04/2017.
 */
public class RepositoryImplForTesting extends RepositoryImpl {
    public RepositoryImplForTesting() {
        super(new TestSystemProperties());
    }

    @Override
    public synchronized void addStorageRow(UscAddress addr, DataWord key, DataWord value) {
        super.addStorageRow(addr, key, value);
        AccountState accountState = getAccountState(addr);
        ContractDetails details = getDetailsDataStore().get(addr);
        accountState.setStateRoot(details.getStorageHash());
        updateAccountState(addr, accountState);
    }

    @Override
    public synchronized void addStorageBytes(UscAddress addr, DataWord key, byte[] value) {
        super.addStorageBytes(addr, key, value);
        AccountState accountState = getAccountState(addr);
        ContractDetails details = getDetailsDataStore().get(addr);
        accountState.setStateRoot(details.getStorageHash());
        updateAccountState(addr, accountState);
    }
}
