/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 0.2.0
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */

package com.qcadoo.mes.model.validators;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.qcadoo.mes.api.Entity;
import com.qcadoo.mes.beans.sample.CustomEntityService;
import com.qcadoo.mes.beans.sample.SampleSimpleDatabaseObject;
import com.qcadoo.mes.internal.DataAccessTest;
import com.qcadoo.mes.internal.DefaultEntity;

public class ValidatorTest extends DataAccessTest {

    @Before
    public void init() {
        given(applicationContext.getBean(CustomEntityService.class)).willReturn(new CustomEntityService());
    }

    @Test
    public void shouldHasNoErrorsIfAllFieldAreNotRequired() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", null);
        entity.setField("age", null);

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
        assertTrue(entity.getErrors().isEmpty());
        assertTrue(entity.getGlobalErrors().isEmpty());
    }

    @Test
    public void shouldHasErrorMessage() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("age", "");

        fieldDefinitionAge.withValidator(fieldValidatorFactory.required());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
        assertEquals(1, entity.getErrors().size());
        assertEquals("core.validate.field.error.missing", entity.getError("age").getMessage());
        assertEquals(0, entity.getGlobalErrors().size());
    }

    @Test
    public void shouldHasCustomErrorMessage() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("age", "");

        fieldDefinitionAge.withValidator(fieldValidatorFactory.required().customErrorMessage("missing age"));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
        assertEquals(1, entity.getErrors().size());
        assertEquals("missing age", entity.getError("age").getMessage());
        assertEquals(0, entity.getGlobalErrors().size());
    }

    @Test
    public void shouldBeRequiredIfHasRequiredValidator() throws Exception {
        // given
        fieldDefinitionName.withValidator(fieldValidatorFactory.required());

        // then
        assertTrue(fieldDefinitionName.isRequired());
    }

    @Test
    public void shouldNotBeRequiredIfDoesNotHasRequiredValidator() throws Exception {
        // then
        assertFalse(fieldDefinitionName.isRequired());
    }

    @Test
    public void shouldHasErrorIfIntegerTypeIsWrong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("age", "21w");

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorIfBigDecimalTypeIsWrong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("money", "221.2w");

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorIfDateTypeIsWrong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("money", "2010-01-a");

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorIfBooleanTypeIsWrong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("money", "a");

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldBeUniqueIfHasUniqueValidator() throws Exception {
        // given
        fieldDefinitionName.withValidator(fieldValidatorFactory.unique());

        // then
        assertTrue(fieldDefinitionName.isUnique());
    }

    @Test
    public void shouldNotBeUniqueIfDoesNotHasUniqueValidator() throws Exception {
        // then
        assertFalse(fieldDefinitionName.isUnique());
    }

    @Test
    public void shouldHasErrorsIfRequiredFieldsAreNotSet() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "");
        entity.setField("age", null);

        fieldDefinitionName.withValidator(fieldValidatorFactory.required());
        fieldDefinitionAge.withValidator(fieldValidatorFactory.required());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfStringValueIsTooLong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "qwerty");

        fieldDefinitionName.withValidator(fieldValidatorFactory.length(null, null, 5));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfStringValueIsOutsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "ddd");

        fieldDefinitionName.withValidator(fieldValidatorFactory.range("a", "c", false));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfStringValueIsInsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "bbb");

        fieldDefinitionName.withValidator(fieldValidatorFactory.range("a", "c", false));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfIntegerValueIsOutsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("age", "11");

        fieldDefinitionAge.withValidator(fieldValidatorFactory.range(null, 10, false));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfIntegerValueIsInsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("age", 5);

        fieldDefinitionAge.withValidator(fieldValidatorFactory.range(4, null, false));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfBigDecimalValueIsOutsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("money", "31.22");

        fieldDefinitionMoney.withValidator(fieldValidatorFactory.range(40, 50, false));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfBigDecimalValueIsInsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("money", "31.22");

        fieldDefinitionMoney.withValidator(fieldValidatorFactory.range(30, 40, false));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfDateValueIsOutsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("birthDate", "2010-01-01");

        fieldDefinitionBirthDate.withValidator(fieldValidatorFactory.range(new Date(), new Date(), false));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfDateValueIsInsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("birthDate", "2010-01-01");

        fieldDefinitionBirthDate.withValidator(fieldValidatorFactory.range(null, new Date(), false));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoCheckRangeOfBoolean() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("retired", "false");

        fieldDefinitionRetired.withValidator(fieldValidatorFactory.range(true, true, false));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfIntegerValueIsTooLong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("age", 123456);

        fieldDefinitionAge.withValidator(fieldValidatorFactory.length(null, null, 5));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfBigDecimalValueIsTooLong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("money", new BigDecimal("123.456"));

        fieldDefinitionMoney.withValidator(fieldValidatorFactory.length(null, null, 5));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfBigDecimalPresicionAndScaleAreTooLong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("money", new BigDecimal("123.456"));

        fieldDefinitionMoney.withValidator(fieldValidatorFactory.scale(null, null, 2)).withValidator(
                fieldValidatorFactory.precision(null, null, 6));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfBigDecimalValueLenghtIsOk() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("money", new BigDecimal("123.4"));

        fieldDefinitionMoney.withValidator(fieldValidatorFactory.length(null, null, 5));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfBigDecimalValuePresicionAndScaleIsOk() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("money", new BigDecimal("123.4"));

        fieldDefinitionMoney.withValidator(fieldValidatorFactory.precision(null, null, 4)).withValidator(
                fieldValidatorFactory.scale(null, null, 1));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoCheckLenghtOfBoolean() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("retired", false);

        fieldDefinitionRetired.withValidator(fieldValidatorFactory.length(null, null, 0));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoCheckLenghtOfDate() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("birthDate", "2010-01-01");

        fieldDefinitionBirthDate.withValidator(fieldValidatorFactory.length(null, null, 0));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfStringValueLenghtIsOk() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "qwert");

        fieldDefinitionName.withValidator(fieldValidatorFactory.length(null, null, 5));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfFieldIsNotDuplicated() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "not existed");

        given(criteria.uniqueResult()).willReturn(0);

        fieldDefinitionName.withValidator(fieldValidatorFactory.unique());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfUpdatedFieldIsNotDuplicated() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName(), 1L);
        entity.setField("name", "not existed");

        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);

        given(session.get(SampleSimpleDatabaseObject.class, 1L)).willReturn(databaseObject);
        given(criteria.uniqueResult()).willReturn(0);

        fieldDefinitionName.withValidator(fieldValidatorFactory.unique());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfFieldIsDuplicated() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "existed");

        given(criteria.uniqueResult()).willReturn(1);

        fieldDefinitionName.withValidator(fieldValidatorFactory.unique());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorIfCustomValidatorReturnsTrue() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "qwerty");

        fieldDefinitionName.withValidator(fieldValidatorFactory.custom(hookFactory.getHook(CustomEntityService.class.getName(),
                "isEqualToQwerty")));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHaveErrorIfCustomValidatorReturnsFalse() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "qwert");

        fieldDefinitionName.withValidator(fieldValidatorFactory.custom(hookFactory.getHook(CustomEntityService.class.getName(),
                "isEqualToQwerty")));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
        assertEquals(1, entity.getErrors().size());
        assertEquals("core.validate.field.error.custom", entity.getError("name").getMessage());
        assertEquals(0, entity.getGlobalErrors().size());
    }

    @Test
    public void shouldHaveErrorIfCustomValidationMethodDoesNotExists() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "qwerty");

        fieldDefinitionName.withValidator(fieldValidatorFactory.custom(hookFactory.getHook(CustomEntityService.class.getName(),
                "isEqualToQwertz")));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorIfCustomEntityValidatorReturnsTrue() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "Mr T");
        entity.setField("age", "18");

        dataDefinition.withValidator(fieldValidatorFactory.customEntity(hookFactory.getHook(CustomEntityService.class.getName(),
                "hasAge18AndNameMrT")));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHaveErrorIfCustomEntityValidatorReturnsFalse() throws Exception {
        // given
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "Mr");
        entity.setField("age", "18");

        dataDefinition.withValidator(fieldValidatorFactory.customEntity(hookFactory.getHook(CustomEntityService.class.getName(),
                "hasAge18AndNameMrT")));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHaveErrorIfCustomEntityValidationMethodDoesNotExists() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition.getPluginIdentifier(), dataDefinition.getName());
        entity.setField("name", "Mr T");
        entity.setField("age", "18");

        dataDefinition.withValidator(fieldValidatorFactory.customEntity(hookFactory.getHook(CustomEntityService.class.getName(),
                "hasAge18AndNameMrX")));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

}
