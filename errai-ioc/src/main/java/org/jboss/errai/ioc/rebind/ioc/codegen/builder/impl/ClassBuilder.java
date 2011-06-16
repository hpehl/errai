/*
 * Copyright 2011 JBoss, a divison Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ioc.rebind.ioc.codegen.builder.impl;

import org.jboss.errai.ioc.rebind.ioc.codegen.Builder;
import org.jboss.errai.ioc.rebind.ioc.codegen.Context;
import org.jboss.errai.ioc.rebind.ioc.codegen.MetaClassFactory;
import org.jboss.errai.ioc.rebind.ioc.codegen.Statement;
import org.jboss.errai.ioc.rebind.ioc.codegen.builder.*;
import org.jboss.errai.ioc.rebind.ioc.codegen.builder.callstack.LoadClassReference;
import org.jboss.errai.ioc.rebind.ioc.codegen.meta.MetaClass;

import java.util.*;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class ClassBuilder implements
        ClassDefinitionBuilderScope,
        ClassDefinitionBuilderAbstractOption,
        BaseClassStructureBuilder<BaseClassStructureBuilder> {

  private Context context;

  private String className;
  private Scope scope;
  private MetaClass parent;

  private Set<MetaClass> interfaces = new HashSet<MetaClass>();

  private List<Builder> constructors = new ArrayList<Builder>();
  private List<Builder> fields = new ArrayList<Builder>();
  private List<Builder> methods = new ArrayList<Builder>();

  private StringBuilder buf = new StringBuilder();

  private boolean isAbstract;

  ClassBuilder(String className, MetaClass parent, Context context) {
    this.className = className;
    this.parent = parent;
    this.context = context;
  }

  public static ClassBuilder define(String fullyQualifiedName) {
    return new ClassBuilder(fullyQualifiedName, null, Context.create());
  }

  public static ClassBuilder define(String fullQualifiedName, MetaClass parent) {
    return new ClassBuilder(fullQualifiedName, parent, Context.create());
  }

  private String getSimpleName() {
    int idx = className.lastIndexOf('.');
    if (idx != -1) {
      return className.substring(idx + 1);
    }
    return className;
  }

  public ClassBuilder abstractClass() {
    isAbstract = true;
    return this;
  }

  public ClassBuilder importsClass(Class<?> clazz) {
    return importsClass(MetaClassFactory.get(clazz));
  }

  public ClassBuilder importsClass(MetaClass clazz) {
    context.addClassImport(clazz);
    return this;
  }

  public ClassDefinitionBuilderInterfaces implementsInterface(Class<?> clazz) {
    return implementsInterface(MetaClassFactory.get(clazz));
  }

  public ClassDefinitionBuilderInterfaces implementsInterface(MetaClass clazz) {
    interfaces.add(clazz);
    return this;
  }

  public BaseClassStructureBuilder<BaseClassStructureBuilder> body() {
    return null;
  }

  public ClassDefinitionBuilderAbstractOption publicScope() {
    scope = Scope.Public;
    return this;
  }

  public ClassDefinitionBuilderAbstractOption privateScope() {
    scope = Scope.Private;
    return this;
  }

  public ClassDefinitionBuilderAbstractOption protectedScope() {
    scope = Scope.Protected;
    return this;
  }

  public ClassDefinitionBuilderAbstractOption packageScope() {
    scope = Scope.Package;
    return this;
  }

  public BlockBuilder<BaseClassStructureBuilder> publicConstructor(MetaClass... parms) {
    return genConstructor(Scope.Public, DefParameters.fromTypeArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> publicConstructor(Class<?>... parms) {
    return publicConstructor(MetaClassFactory.fromClassArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> publicConstructor(Parameter... parms) {
    return genConstructor(Scope.Public, DefParameters.fromParameters(parms));
  }


  public BlockBuilder<BaseClassStructureBuilder> privateConstructor(MetaClass... parms) {
    return genConstructor(Scope.Private, DefParameters.fromTypeArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> privateConstructor(Class<?>... parms) {
    return privateConstructor(MetaClassFactory.fromClassArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> privateConstructor(Parameter... parms) {
    return genConstructor(Scope.Private, DefParameters.fromParameters(parms));
  }


  public BlockBuilder<BaseClassStructureBuilder> protectedConstructor(MetaClass... parms) {
    return genConstructor(Scope.Protected, DefParameters.fromTypeArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> protectedConstructor(Class<?>... parms) {
    return protectedConstructor(MetaClassFactory.fromClassArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> protectedConstructor(Parameter... parms) {
    return genConstructor(Scope.Protected, DefParameters.fromParameters(parms));
  }


  public BlockBuilder<BaseClassStructureBuilder> packageConstructor(MetaClass... parms) {
    return genConstructor(Scope.Package, DefParameters.fromTypeArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> packageConstructor(Class<?>... parms) {
    return packageConstructor(MetaClassFactory.fromClassArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> packageConstructor(Parameter... parms) {
    return genConstructor(Scope.Package, DefParameters.fromParameters(parms));
  }


  private BlockBuilder<BaseClassStructureBuilder> genConstructor(final Scope scope, final DefParameters
          defParameters) {
    return new BlockBuilder<BaseClassStructureBuilder>(new BuildCallback<BaseClassStructureBuilder>() {
      public BaseClassStructureBuilder callback(final Statement statement) {
        constructors.add(new Builder() {
          public String toJavaString() {
            return new StringBuilder().append(scope.getCanonicalName())
                    .append(" ")
                    .append(getSimpleName())
                    .append(defParameters.generate(context))
                    .append(" {\n").append(statement.generate(context)).append("\n}\n")
                    .toString();
          }
        });

        return ClassBuilder.this;
      }
    });
  }

  public BlockBuilder<BaseClassStructureBuilder> publicMethod(MetaClass returnType, String name, MetaClass... parms) {
    return genMethod(Scope.Public, returnType, name, DefParameters.fromTypeArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> publicMethod(Class<?> returnType, String name, Class<?>... parms) {
    return publicMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> publicMethod(MetaClass returnType, String name, Parameter... parms) {
    return genMethod(Scope.Public, returnType, name, DefParameters.fromParameters(parms));
  }


  public BlockBuilder<BaseClassStructureBuilder> privateMethod(MetaClass returnType, String name, MetaClass... parms) {
    return genMethod(Scope.Private, returnType, name, DefParameters.fromTypeArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> privateMethod(Class<?> returnType, String name, Class<?>... parms) {
    return privateMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> privateMethod(MetaClass returnType, String name, Parameter... parms) {
    return genMethod(Scope.Private, returnType, name, DefParameters.fromParameters(parms));
  }


  public BlockBuilder<BaseClassStructureBuilder> protectedMethod(MetaClass returnType, String name, MetaClass... parms) {
    return genMethod(Scope.Protected, returnType, name, DefParameters.fromTypeArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> protectedMethod(Class<?> returnType, String name, Class<?>... parms) {
    return protectedMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> protectedMethod(MetaClass returnType, String name, Parameter... parms) {
    return genMethod(Scope.Protected, returnType, name, DefParameters.fromParameters(parms));
  }


  public BlockBuilder<BaseClassStructureBuilder> packageMethod(MetaClass returnType, String name, MetaClass... parms) {
    return genMethod(Scope.Package, returnType, name, DefParameters.fromTypeArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> packageMethod(Class<?> returnType, String name, Class<?>... parms) {
    return packageMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  public BlockBuilder<BaseClassStructureBuilder> packageMethod(MetaClass returnType, String name, Parameter... parms) {
    return genMethod(Scope.Package, returnType, name, DefParameters.fromParameters(parms));
  }

  private BlockBuilder<BaseClassStructureBuilder> genMethod(final Scope scope,
                                                            final MetaClass returnType,
                                                            final String name,
                                                            final DefParameters defParameters) {
    return new BlockBuilder<BaseClassStructureBuilder>(new BuildCallback<BaseClassStructureBuilder>() {
      public BaseClassStructureBuilder callback(final Statement statement) {
        methods.add(new Builder() {
          public String toJavaString() {
            return new StringBuilder().append(scope.getCanonicalName())
                    .append(" ")
                    .append(LoadClassReference.getClassReference(returnType, context))
                    .append(" ")
                    .append(name)
                    .append(defParameters.generate(context))
                    .append(" {\n").append(statement.generate(context)).append("\n}\n")
                    .toString();
          }
        });

        return ClassBuilder.this;
      }
    });
  }

  public FieldBuilder<BaseClassStructureBuilder> publicField(String name, MetaClass type) {
    return null;
  }

  public FieldBuilder<BaseClassStructureBuilder> publicField(String name, Class<?> type) {
    return null;
  }

  public FieldBuilder<BaseClassStructureBuilder> privateField(String name, MetaClass type) {
    return null;
  }

  public FieldBuilder<BaseClassStructureBuilder> privateField(String name, Class<?> type) {
    return null;
  }

  public FieldBuilder<BaseClassStructureBuilder> protectedField(String name, MetaClass type) {
    return null;
  }

  public FieldBuilder<BaseClassStructureBuilder> protectedField(String name, Class<?> type) {
    return null;
  }

  public FieldBuilder<BaseClassStructureBuilder> packageField(String name, MetaClass type) {
    return null;
  }

  public FieldBuilder<BaseClassStructureBuilder> packageField(String name, Class<?> type) {
    return null;
  }

  private FieldBuilder<BaseClassStructureBuilder> genField(String name, MetaClass type) {
    return new FieldBuilder<BaseClassStructureBuilder>(new BuildCallback<BaseClassStructureBuilder>() {
      public BaseClassStructureBuilder callback(final Statement statement) {
        fields.add(new Builder() {
          public String toJavaString() {
            return statement.generate(context);
          }
        });

        return ClassBuilder.this;
      }
    });

  }

  public String toJavaString() {
    return null;
  }
}
