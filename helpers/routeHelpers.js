const Joi = require('@hapi/joi');

module.exports = {
    validateBody: (schema) => {
        return (req, res, next) => {
            const result = schema.validate(req.body);
            console.log('req.body', req.body);
            if (result.error) {
                return res.status(400).json(result.error);
            }

            // req.value.body instead of req.body to access body values
            if (!req.value) { req.value = {}; }
            req.value['body'] = result.value;
            //So that this validation middleware doesn't block the controllers
            next();
        }
    },

    /* 
     * email() checks email validity, required() means field is required;
     * we can have min() and max() for fields as well.
     */
    schemas: {
        authSchema: Joi.object({
            email: Joi.string().email().required(),
            password: Joi.string().required()
        })
    }
}