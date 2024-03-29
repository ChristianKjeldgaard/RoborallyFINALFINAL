/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.dal;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 * Databasen bliver koblet til programmet for at gemme
 * dataen, hvilket betyder at programmet kan afsluttes efter gemmelsen, og kan efterfølgende blive
 * indhentet som tidligere. Implementeringen og etableringen af forbindelsen til databasen foregår i
 * vores DAL-mappe (Data Access Layer).
 * 
 * Koden bruges til at oprette en statisk forekomst af Repository-klassen ved hjælp af Connector-klassen. 
 * Koden tjekker, om der er en forekomst af Repository, og hvis ikke, opretter den en ved hjælp af Connector. 
 * Det returnerer det nyoprettede Repository-objekt til den, der har bedt om det.

 */
public class RepositoryAccess {
    
	private static Repository repository;
	
	public static IRepository getRepository() {
		if(repository == null) {
			repository = new Repository(new Connector());
		}
		return repository;
	}
	
}
