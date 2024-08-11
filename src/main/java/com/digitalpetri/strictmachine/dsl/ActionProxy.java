/*
 * Copyright (c) 2024 Kevin Herron
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package com.digitalpetri.strictmachine.dsl;

@FunctionalInterface
public interface ActionProxy<S, E> {

  /**
   * Execute this action.
   *
   * @param context the {@link ActionContext}.
   * @param action the {@link Action} to execute.
   */
  void execute(ActionContext<S, E> context, Action<S, E> action);

}
