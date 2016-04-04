/**
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the Light-Jason                                               #
 * # Copyright (c) 2015-16, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package lightjason.language.execution.fuzzy;

import java.util.stream.Collector;


/**
 * defines a fuzzy t-norm
 *
 * @see https://en.wikipedia.org/wiki/T-norm_fuzzy_logics
 * @see https://en.wikipedia.org/wiki/Fuzzy_set_operations
 * @see https://en.wikipedia.org/wiki/Construction_of_t-norms
 * @see https://zackehh.com/collecting-a-java-8-stream-into-a-jackson-arraynode/
 * @see http://blog.radoszewski.pl/programming/java/2015/07/31/custom-java-8-collectors.html
 */
public interface IFuzzyOperator<T> extends Collector<IFuzzyValue<T>, IFuzzyValueMutable<T>, IFuzzyValue<T>>
{

}