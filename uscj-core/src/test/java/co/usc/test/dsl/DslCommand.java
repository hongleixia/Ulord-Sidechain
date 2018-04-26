/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
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

package co.usc.test.dsl;

import java.util.List;

/**
 * Created by ajlopez on 8/6/2016.
 */
public class DslCommand {
    private String verb;
    private List<String> arguments;

    public DslCommand(String verb) {
        this.verb = verb;
    }

    public DslCommand(String verb, List<String> arguments) {
        this.verb = verb;
        this.arguments = arguments;
    }

    public String getVerb() { return this.verb; }

    public boolean isCommand(String verb) {
        return this.verb.equals(verb);
    }

    public int getArity() {
        if (this.arguments == null)
            return 0;

        return this.arguments.size();
    }

    public String getArgument(int position) {
        if (this.arguments == null || this.arguments.size() <= position)
            return null;

        return this.arguments.get(position);
    }
}
