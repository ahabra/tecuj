/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2004  Abdul Habra, Doug Estep
www.tek271.com

This file is part of TECUJ.

TECUJ is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; version 2.

TECUJ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with TECUJ; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

You can contact the author at ahabra@yahoo.com
*/

package com.tek271.util;

/**
 * <p>An interface that allows executing a method and returning a success/failed code.
 * @version 1.0
 * @author Doug Estep, Abdul Habra
 */
public interface IExecutable {

/**
 * Executes the business logic.
 * @return Returns <code>null</code> if the process was not successful;
 * <code>non-null</code> if success.
 */
    public boolean execute();
} // IExecutable
